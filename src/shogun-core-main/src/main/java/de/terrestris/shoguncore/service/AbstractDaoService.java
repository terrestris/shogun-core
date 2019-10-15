package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.GenericHibernateDao;
import de.terrestris.shoguncore.model.PersistentObject;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * This abstract service class simply provides a data access object for the type
 * {@link E} (and a logger).
 *
 * @author Nils Bühner
 */
@Transactional(value = "transactionManager")
public abstract class AbstractDaoService<E extends PersistentObject, D extends GenericHibernateDao<E, Integer>> {

    /**
     * The LOGGER instance (that will be available in all subclasses)
     */
    protected static final Logger logger = getLogger(AbstractDaoService.class);

    /**
     * Provides the concrete entity class of the controller.
     * Based on the pattern proposed here: http://stackoverflow.com/a/3403987
     */
    private final Class<E> entityClass;
    /**
     * The data access object
     */
    protected D dao;

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected AbstractDaoService(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return the dao
     */
    public D getDao() {
        return dao;
    }

    /**
     * Subclasses must implement this class and annotate it with
     * {@link Autowired} and {@link Qualifier}! This is necessary as there may
     * be multiple candidates to autowire (due to hierarchy) and we have to
     * configure the correct ones.
     *
     * @param dao the dao to set
     */
    public abstract void setDao(D dao);

    /**
     * @return the entityClass
     */
    public Class<E> getEntityClass() {
        return entityClass;
    }

}
