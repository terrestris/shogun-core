package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.PluginDao;
import de.terrestris.shogun2.model.Plugin;

/**
 * Service class for the {@link Plugin} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("pluginService")
public class PluginService<E extends Plugin, D extends PluginDao<E>> extends
		PermissionAwareCrudService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public PluginService() {
		this((Class<E>) Plugin.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected PluginService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("pluginDao")
	public void setDao(D dao) {
		this.dao = dao;
	}
}
