package de.terrestris.shogun2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import javax.sql.rowset.serial.SerialException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * This service class will be used by the {@link ContentInitializer} to create content
 * on initialization.
 *
 * @author Nils Bühner
 */
@Service("initializationService")
@Transactional(value = "transactionManager")
@DependsOn("userService")
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
        Shogun2JsonObjectMapper mapper = applicationContext.getBean(Shogun2JsonObjectMapper.class);
        Reflections reflections = new Reflections("de.terrestris");
        Set<Class<? extends GenericHibernateDao>> daos = reflections.getSubTypesOf(GenericHibernateDao.class);
        daos.stream().forEach(cls -> {
            Map<String, ? extends GenericHibernateDao> daoMap = applicationContext.getBeansOfType(cls);
            daoMap.values().stream().forEach(dao -> {
                if (dao.getEntityClass().isAnnotationPresent(RootObject.class)) {
                    List<? extends PersistentObject> all = dao.findAll();
                    Map<Integer, String> jsons = new LinkedHashMap<>();
                    all.stream().forEach(entity -> {
                        try {
                            String json = mapper.writeValueAsString(entity);
                            de.terrestris.shogun2.model.storage.RootObject blob = new de.terrestris.shogun2.model.storage.RootObject();
                            blob.setJson(new SerialBlob(json.getBytes("UTF-8")));
                            blob.setType(dao.getEntityClass().getCanonicalName());
                            rootObjectDao.saveOrUpdate(blob);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        } catch (SerialException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
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
