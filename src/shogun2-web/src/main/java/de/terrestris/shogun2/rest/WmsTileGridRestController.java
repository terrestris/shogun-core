package de.terrestris.shogun2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.WmsTileGridDao;
import de.terrestris.shogun2.model.layer.util.WmsTileGrid;
import de.terrestris.shogun2.service.WmsTileGridService;

/**
 * @author Kai Volland
 * @author Nils Bühner
 *
 */
@RestController
@RequestMapping("/wmstilegrids")
public class WmsTileGridRestController<E extends WmsTileGrid, D extends WmsTileGridDao<E>, S extends WmsTileGridService<E, D>>
		extends AbstractRestController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public WmsTileGridRestController() {
		this((Class<E>) WmsTileGrid.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected WmsTileGridRestController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("wmsTileGridService")
	public void setService(S service) {
		this.service = service;
	}
}
