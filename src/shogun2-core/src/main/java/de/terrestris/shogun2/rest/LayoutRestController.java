package de.terrestris.shogun2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.LayoutDao;
import de.terrestris.shogun2.model.layout.Layout;
import de.terrestris.shogun2.service.LayoutService;

/**
 * @author Kai Volland
 * @author Nils Bühner
 *
 */
@RestController
@RequestMapping("/layouts")
public class LayoutRestController<E extends Layout, D extends LayoutDao<E>, S extends LayoutService<E, D>>
		extends AbstractRestController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public LayoutRestController() {
		this((Class<E>) Layout.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected LayoutRestController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("layoutService")
	public void setService(S service) {
		this.service = service;
	}
}
