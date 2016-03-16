package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layer.util.TileGrid;

@Repository("tileGridDao")
public class TileGridDao<E extends TileGrid> extends
		GenericHibernateDao<E, Integer> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public TileGridDao() {
		super((Class<E>) TileGrid.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected TileGridDao(Class<E> clazz) {
		super(clazz);
	}

}
