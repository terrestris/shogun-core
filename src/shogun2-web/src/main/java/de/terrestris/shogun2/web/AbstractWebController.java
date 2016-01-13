package de.terrestris.shogun2.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.service.AbstractCrudService;

/**
 *
 * This abstract controller class provides basic web controller functionality.
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
	 *
	 * @param data
	 * @return
	 */
	protected Map<String, Object> getModelMapSuccess(List<? extends Object> data) {

		Map<String, Object> returnMap = new HashMap<String, Object>(3);
		returnMap.put("total", data.size());
		returnMap.put("data", data);
		returnMap.put("success", true);

		return returnMap;
	}

	/**
	 *
	 * @param data
	 * @return
	 */
	protected Map<String, Object> getModelMapSuccess(Set<? extends Object> data) {

		Map<String, Object> returnMap = new HashMap<String, Object>(3);
		returnMap.put("total", data.size());
		returnMap.put("data", data);
		returnMap.put("success", true);

		return returnMap;
	}

	/**
	 *
	 * @param dataset
	 * @return
	 */
	protected Map<String, Object> getModelMapSuccess(Object dataset) {

		Map<String, Object> returnMap = new HashMap<String, Object>(3);
		returnMap.put("total", 1);
		returnMap.put("data", dataset);
		returnMap.put("success", true);

		return returnMap;
	}

	/**
	 *
	 * @param msg
	 * @return
	 */
	protected Map<String, Object> getModelMapError(String msg) {

		Map<String, Object> returnMap = new HashMap<String, Object>(2);
		returnMap.put("message", msg);
		returnMap.put("success", false);

		return returnMap;
	}

	/**
	 * @return the service
	 */
	public S getService() {
		return service;
	}

}
