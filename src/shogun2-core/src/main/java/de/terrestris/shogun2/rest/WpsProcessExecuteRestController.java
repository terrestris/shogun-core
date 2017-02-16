package de.terrestris.shogun2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.WpsProcessExecuteDao;
import de.terrestris.shogun2.model.wps.WpsProcessExecute;
import de.terrestris.shogun2.service.WpsProcessExecuteService;

/**
 * @author Nils Bühner
 *
 */
@RestController
@RequestMapping("/wpsprocessexecutes")
public class WpsProcessExecuteRestController<E extends WpsProcessExecute, D extends WpsProcessExecuteDao<E>, S extends WpsProcessExecuteService<E, D>>
		extends AbstractRestController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public WpsProcessExecuteRestController() {
		this((Class<E>) WpsProcessExecute.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected WpsProcessExecuteRestController(Class<E> entityClass) {
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
