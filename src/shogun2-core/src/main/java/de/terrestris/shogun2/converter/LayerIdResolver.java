package de.terrestris.shogun2.converter;

import de.terrestris.shogun2.model.layer.Layer;

/**
 *
 * @author Nils Buehner
 *
 */
public class LayerIdResolver<E extends Layer> extends
		PersistentObjectIdResolver<Layer> {

	/**
	 * Default Constructor
	 */
	public LayerIdResolver() {
		super(Layer.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param entityClass
	 */
	@SuppressWarnings("unchecked")
	protected LayerIdResolver(Class<E> entityClass) {
		super((Class<Layer>) entityClass);
	}

}
