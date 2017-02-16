package de.terrestris.shogun2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.WpsReferenceDao;
import de.terrestris.shogun2.model.wps.WpsReference;
import de.terrestris.shogun2.service.WpsReferenceService;

/**
 * @author Nils Bühner
 *
 */
@RestController
@RequestMapping("/wpsreferences")
public class WpsReferenceRestController<E extends WpsReference, D extends WpsReferenceDao<E>, S extends WpsReferenceService<E, D>>
		extends AbstractRestController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public WpsReferenceRestController() {
		this((Class<E>) WpsReference.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected WpsReferenceRestController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("wpsReferenceService")
	public void setService(S service) {
		this.service = service;
	}
}
