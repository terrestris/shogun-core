/**
 *
 */
package de.terrestris.shogun2.web;

import de.terrestris.shogun2.dao.AbstractLayerDao;
import de.terrestris.shogun2.model.layer.AbstractLayer;
import de.terrestris.shogun2.service.AbstractLayerService;

/**
 *
 * @author Johannes Weskamm
 * @author Kai Volland
 * @author Nils Bühner
 *
 */
public abstract class AbstractLayerController<E extends AbstractLayer, D extends AbstractLayerDao<E>, S extends AbstractLayerService<E, D>>
		extends AbstractWebController<E, D, S> {

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected AbstractLayerController(Class<E> entityClass) {
		super(entityClass);
	}

}
