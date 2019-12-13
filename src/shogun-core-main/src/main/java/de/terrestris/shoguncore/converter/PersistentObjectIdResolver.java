package de.terrestris.shoguncore.converter;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import de.terrestris.shoguncore.dao.GenericHibernateDao;
import de.terrestris.shoguncore.model.PersistentObject;
import de.terrestris.shoguncore.service.AbstractCrudService;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * An ID resolver for {@link PersistentObject}s when deserializing only on the
 * base of ID values. Based on a given ID, this resolver will load the whole
 * entity from the database. Extends the default implementation.
 *
 * @author Nils Buehner
 */
public abstract class PersistentObjectIdResolver<E extends PersistentObject, D extends GenericHibernateDao<E, Integer>, S extends AbstractCrudService<E, D>>
    extends SimpleObjectIdResolver {

    protected static final Logger logger = getLogger(PersistentObjectIdResolver.class);

    protected S service;

    /**
     * Default Constructor that injects beans automatically.
     */
    public PersistentObjectIdResolver() {
        // As subclasses of this class are used in the resolver property of an
        // JsonIdentityInfo annotation, we cannot easily autowire components
        // (like the service for the current). For that reason, we use this
        // helper method to process the injection of the services
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    /**
     *
     */
    @Override
    public void bindItem(IdKey id, Object ob) {
        super.bindItem(id, ob);
    }

    /**
     *
     */
    @Override
    public E resolveId(IdKey idKey) {
        try {
            if (idKey.key instanceof Integer) {
                final Integer id = (Integer) idKey.key;
                // we only "load" the entity to follow a lazy approach, i.e.
                // requests to the database will only be queried if any properties
                // of the entity will (later) be accessed (which may already
                // the case when jackson is doing some magic)
                return service.loadById(id);
            } else {
                throw new Exception("ID is not of type Integer.");
            }
        } catch (Exception e) {
            logger.error("Could not resolve object by ID: " + e.getMessage());
            return null;
        }
    }

    /**
     *
     */
    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        try {
            return getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Error instantiating ObjectIdResolver: " + e.getMessage());
        }
        return null;
    }

    /**
     * @return the service
     */
    public S getService() {
        return service;
    }

    /**
     * Has to be implemented by subclasses to autowire and set the correct
     * service class.
     *
     * @param service the service to set
     */
    public abstract void setService(S service);
}
