package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.wps.WpsParameter;


/**
 *
 * @author Nils Bühner
 *
 */
@Repository("wpsParameterDao")
public class WpsParameterDao<E extends WpsParameter>
		extends GenericHibernateDao<E, Integer> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public WpsParameterDao() {
		super((Class<E>) WpsParameter.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected WpsParameterDao(Class<E> clazz) {
		super(clazz);
	}
}
