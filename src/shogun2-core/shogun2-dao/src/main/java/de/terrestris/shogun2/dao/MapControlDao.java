package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.map.MapControl;

@Repository("mapControlDao")
public class MapControlDao<E extends MapControl> extends
		GenericHibernateDao<E, Integer> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public MapControlDao() {
		super((Class<E>) MapControl.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected MapControlDao(Class<E> clazz) {
		super(clazz);
	}

}
