package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layer.util.WmsTileGrid;

@Repository("wmsTileGridDao")
public class WmsTileGridDao<E extends WmsTileGrid> extends
		GenericHibernateDao<E, Integer> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public WmsTileGridDao() {
		super((Class<E>) WmsTileGrid.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected WmsTileGridDao(Class<E> clazz) {
		super(clazz);
	}

}
