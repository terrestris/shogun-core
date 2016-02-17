package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layer.AbstractLayer;

@Repository("abstractLayerDao")
public class AbstractLayerDao<E extends AbstractLayer> extends
		GenericHibernateDao<E, Integer> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public AbstractLayerDao() {
		super((Class<E>) AbstractLayer.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected AbstractLayerDao(Class<E> clazz) {
		super(clazz);
	}

}
