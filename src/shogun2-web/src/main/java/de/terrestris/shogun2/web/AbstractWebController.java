package de.terrestris.shogun2.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.service.AbstractCrudService;

/**
 *
 * This abstract controller class provides basic web controller functionality:
 * A logger and a service.
 *
 * @author Daniel Koch
 *
 */
public abstract class AbstractWebController<E extends PersistentObject, D extends GenericHibernateDao<E, Integer>, S extends AbstractCrudService<E, D>> {

	/**
	 * The LOGGER instance (that will be available in all subclasses)
	 */
	protected final Logger LOG = Logger.getLogger(getClass());

	/**
	 * The {@link AbstractCrudService} for this controller.
	 */
	protected S service;

	/**
	 *
	 * Subclasses must implement this class and annotate it with
	 * {@link Autowired} and {@link Qualifier}!
	 *
	 * @param service the service to set
	 */
	public abstract void setService(S service);

	/**
	 * @return the service
	 */
	public S getService() {
		return service;
	}

}
