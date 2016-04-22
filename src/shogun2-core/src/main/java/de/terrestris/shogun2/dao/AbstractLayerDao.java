package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layer.AbstractLayer;

/**
 * This DAO is not abstract (even though the {@link AbstractLayer} class is)
 * because we need to use it for subclasses of {@link AbstractLayer} at some
 * point.
 *
 * @author Nils Bühner
 *
 * @param <E>
 */
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
