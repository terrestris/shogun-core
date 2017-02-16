/**
 *
 */
package de.terrestris.shogun2.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.terrestris.shogun2.dao.WpsPrimitiveDao;
import de.terrestris.shogun2.model.wps.WpsPrimitive;
import de.terrestris.shogun2.service.WpsPrimitiveService;

/**
 *
 * @author Nils Bühner
 *
 */
@Controller
@RequestMapping("/wpsprimitives")
public class WpsPrimitiveController<E extends WpsPrimitive, D extends WpsPrimitiveDao<E>, S extends WpsPrimitiveService<E, D>>
		extends WpsParameterController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public WpsPrimitiveController() {
		this((Class<E>) WpsPrimitive.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected WpsPrimitiveController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("wpsPrimitiveService")
	public void setService(S service) {
		this.service = service;
	}

}
