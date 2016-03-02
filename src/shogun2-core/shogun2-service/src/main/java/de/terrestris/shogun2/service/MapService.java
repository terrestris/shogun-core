package de.terrestris.shogun2.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.MapDao;
import de.terrestris.shogun2.model.layer.AbstractLayer;
import de.terrestris.shogun2.model.module.Map;
import de.terrestris.shogun2.model.module.Module;

/**
 * Service class for the {@link Module} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("mapService")
public class MapService<E extends Map, D extends MapDao<E>> extends
		ModuleService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public MapService() {
		this((Class<E>) Map.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected MapService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("mapDao")
	public void setDao(D dao) {
		this.dao = dao;
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public Set<E> findMapsWithLayer(AbstractLayer layer) {
		return dao.findMapsWithLayer(layer);
	}
}
