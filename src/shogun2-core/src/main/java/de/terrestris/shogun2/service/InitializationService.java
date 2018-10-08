package de.terrestris.shogun2.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.terrestris.shogun2.annotations.RootObject;
import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.dao.RootObjectDao;
import de.terrestris.shogun2.init.ContentInitializer;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.util.json.Shogun2JsonObjectMapper;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * This service class will be used by the {@link ContentInitializer} to create content
 * on initialization.
 *
 * @author Nils Bühner
 */
@Service("initializationService")
@Transactional(value = "transactionManager")
@DependsOn({"userService", "jacksonObjectMapper"})
public class InitializationService implements ApplicationContextAware {

    /**
     * The Logger
     */
    private static final Logger LOG = getLogger(InitializationService.class);

    /**
     * A generic dao that can easily be used for any entity that extends
     * {@link PersistentObject}.
     */
    @Autowired
    @Qualifier("genericDao")
    private GenericHibernateDao<PersistentObject, Integer> dao;

    @Autowired
    @Qualifier("rootObjectDao")
    private RootObjectDao<de.terrestris.shogun2.model.storage.RootObject> rootObjectDao;

    /**
     * The password encoder that is used to encode the password of a user.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    private ApplicationContext applicationContext;

    /**
     * A "generic" method to save an arbitrary {@link PersistentObject}.
     *
     * @param object
     */
    public void savePersistentObject(PersistentObject object) {
        final String type = object.getClass().getSimpleName();
        LOG.trace("Trying to create a new " + type);
        dao.saveOrUpdate(object);
        LOG.info("Created the " + type + " with id " + object.getId());
    }

    /**
     * Used to create a user. Implements special logic by encoding the password.
     *
     * @param user
     * @return
     */
    public void saveUser(User user) {
        LOG.trace("Trying to create a new user");
        // encode the raw password using bcrypt
        final String pwHash = passwordEncoder.encode(user.getPassword());
        user.setPassword(pwHash);
        dao.saveOrUpdate(user);
        LOG.info("Created the user " + user.getAccountName());
    }

    public void initializeJsonStorage() {
        Reflections reflections = new Reflections("de.terrestris");
        Set<Class<?>> rootClasses = reflections.getTypesAnnotatedWith(RootObject.class);
        Map<Class<?>, StdSerializer<PersistentObject>> serializers = new HashMap<>();
        rootClasses.forEach(rootClass -> {
            serializers.put(rootClass, new StdSerializer<PersistentObject>((Class<PersistentObject>) rootClass) {
                @Override
                public void serialize(PersistentObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
                    gen.writeStartObject();
                    gen.writeObjectField("id", value.getId());
                    gen.writeObjectField("isRootObject", true);
                    gen.writeObjectField("@class", value.getClass().getCanonicalName());
                    gen.writeEndObject();
                }

                @Override
                public void serializeWithType(PersistentObject value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
                    WritableTypeId typeId = typeSer.typeId(value, JsonToken.START_OBJECT);
                    typeSer.writeTypePrefix(gen, typeId);
                    gen.writeObjectField("id", value.getId());
                    gen.writeObjectField("isRootObject", true);
                    gen.writeObjectField("@class", value.getClass().getCanonicalName());
                    typeSer.writeTypeSuffix(gen, typeId);
                }
            });
        });

        Set<Class<? extends GenericHibernateDao>> daos = reflections.getSubTypesOf(GenericHibernateDao.class);

        daos.forEach(cls -> {
            Map<String, ? extends GenericHibernateDao> daoMap = applicationContext.getBeansOfType(cls);
            daoMap.values().forEach(dao -> {
                if (dao.getEntityClass().isAnnotationPresent(RootObject.class)) {
                    Shogun2JsonObjectMapper mapper = new Shogun2JsonObjectMapper();
                    SimpleModule module = new SimpleModule();
                    serializers.forEach((clazz, serializer) -> {
                        if (!dao.getEntityClass().isAssignableFrom(clazz) && !clazz.isAssignableFrom(dao.getEntityClass())) {
                            module.addSerializer((Class<PersistentObject>)clazz, serializer);
                        }
                    });
                    mapper.registerModule(module);
                    List<? extends PersistentObject> all = dao.findAll();
                    Map<Integer, String> jsons = new LinkedHashMap<>();
                    all.forEach(entity -> {
                        try {
                            String json = mapper.writeValueAsString(entity);
                            de.terrestris.shogun2.model.storage.RootObject blob = new de.terrestris.shogun2.model.storage.RootObject();
                            blob.setJson(new SerialBlob(json.getBytes(StandardCharsets.UTF_8)));
                            blob.setType(dao.getEntityClass().getCanonicalName());
                            blob.setReferencedId(entity.getId());
                            rootObjectDao.saveOrUpdate(blob);
                        } catch (JsonProcessingException | SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
