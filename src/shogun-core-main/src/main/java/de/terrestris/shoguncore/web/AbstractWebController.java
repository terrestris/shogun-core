package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.dao.GenericHibernateDao;
import de.terrestris.shoguncore.model.PersistentObject;
import de.terrestris.shoguncore.service.AbstractCrudService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * This abstract controller class provides basic web controller functionality:
 * A logger and a service.
 *
 * @author Daniel Koch
 */
public abstract class AbstractWebController<E extends PersistentObject, D extends GenericHibernateDao<E, Integer>, S extends AbstractCrudService<E, D>> {

    /**
     * The LOGGER instance (that will be available in all subclasses)
     */
    protected static final Logger logger = getLogger(AbstractWebController.class);

    /**
     * Provides the concrete entity class of the controller.
     * Based on the pattern propsed here: http://stackoverflow.com/a/3403987
     */
    private final Class<E> entityClass;
    /**
     * The {@link AbstractCrudService} for this controller.
     */
    protected S service;

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected AbstractWebController(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return the service
     */
    public S getService() {
        return service;
    }

    /**
     * Subclasses must implement this class and annotate it with
     * {@link Autowired} and {@link Qualifier}!
     *
     * @param service the service to set
     */
    public abstract void setService(S service);

    /**
     * @return the entityClass
     */
    public Class<E> getEntityClass() {
        return entityClass;
    }

}
