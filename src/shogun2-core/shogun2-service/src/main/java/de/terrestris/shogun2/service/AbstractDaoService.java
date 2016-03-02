package de.terrestris.shogun2.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.PersistentObject;

/**
 * This abstract service class simply provides a data access object for the type
 * {@link E} (and a logger).
 *
 * @author Nils Bühner
 *
 */
@Transactional(value = "transactionManager")
public abstract class AbstractDaoService<E extends PersistentObject, D extends GenericHibernateDao<E, Integer>> {

	/**
	 * The LOGGER instance (that will be available in all subclasses)
	 */
	protected final Logger LOG = Logger.getLogger(getClass());

	/**
	 * Provides the concrete entity class of the controller.
	 * Based on the pattern propsed here: http://stackoverflow.com/a/3403987
	 */
	private final Class<E> entityClass;

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected AbstractDaoService(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * The data access object
	 */
	protected D dao;

	/**
	 * Subclasses must implement this class and annotate it with
	 * {@link Autowired} and {@link Qualifier}! This is necessary as there may
	 * be multiple candidates to autowire (due to hierarchy) and we have to
	 * configure the correct ones.
	 *
	 * @param dao
	 *            the dao to set
	 */
	public abstract void setDao(D dao);

	/**
	 * @return the dao
	 */
	public D getDao() {
		return dao;
	}

	/**
	 * @return the entityClass
	 */
	public Class<E> getEntityClass() {
		return entityClass;
	}

}
