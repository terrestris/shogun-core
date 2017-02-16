/**
 *
 */
package de.terrestris.shogun2.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.terrestris.shogun2.dao.WpsProcessExecuteDao;
import de.terrestris.shogun2.model.wps.WpsProcessExecute;
import de.terrestris.shogun2.service.WpsProcessExecuteService;

/**
 *
 * @author Nils Bühner
 *
 */
@Controller
@RequestMapping("/wpsprocessexecutes")
public class WpsProcessExecuteController<E extends WpsProcessExecute, D extends WpsProcessExecuteDao<E>, S extends WpsProcessExecuteService<E, D>>
		extends WpsReferenceController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public WpsProcessExecuteController() {
		this((Class<E>) WpsProcessExecute.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected WpsProcessExecuteController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("wpsProcessExecuteService")
	public void setService(S service) {
		this.service = service;
	}

}
