package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.wps.WpsPlugin;


/**
 *
 * @author Nils Bühner
 *
 */
@Repository("wpsPluginDao")
public class WpsPluginDao<E extends WpsPlugin> extends PluginDao<E> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public WpsPluginDao() {
		super((Class<E>) WpsPlugin.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected WpsPluginDao(Class<E> clazz) {
		super(clazz);
	}
}
