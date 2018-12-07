package de.terrestris.shogun2.converter;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.service.AbstractCrudService;
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

    protected final Logger LOG = getLogger(getClass());

    protected S service;

    /**
     * Default Constructor that injects beans automatically.
     *
     * @param entityClass
     */
    protected PersistentObjectIdResolver() {
        // As subclasses of this class are used in the resolver property of an
        // JsonIdentityInfo annotation, we cannot easily autowire components
        // (like the service for the current). For that reason, we use this
        // helper method to process the injection of the services
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    /**
     * Has to be implemented by subclasses to autowire and set the correct
     * service class.
     *
     * @param service the service to set
     */
    public abstract void setService(S service);

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
            LOG.error("Could not resolve object by ID: " + e.getMessage());
            return null;
        }

    }

    /**
     *
     */
    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return super.canUseFor(resolverType);
    }

    /**
     *
     */
    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        try {
            return getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("Error instantiating ObjectIdResolver: " + e.getMessage());
        }
        return null;
    }

    /**
     * @return the service
     */
    public S getService() {
        return service;
    }
}
