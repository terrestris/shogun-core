package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.wps.WpsPrimitive;


/**
 *
 * @author Nils Bühner
 *
 */
@Repository("wpsPrimitiveDao")
public class WpsPrimitiveDao<E extends WpsPrimitive> extends WpsParameterDao<E> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public WpsPrimitiveDao() {
		super((Class<E>) WpsPrimitive.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected WpsPrimitiveDao(Class<E> clazz) {
		super(clazz);
	}
}
