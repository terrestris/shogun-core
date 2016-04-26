package de.terrestris.shogun2.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.AbstractLayerDao;
import de.terrestris.shogun2.dao.MapDao;
import de.terrestris.shogun2.model.layer.AbstractLayer;
import de.terrestris.shogun2.model.module.Map;

/**
 * Service class for the {@link AbstractLayer} model.
 *
 * This service is not abstract (even though the {@link AbstractLayer} class is)
 * because we need to use it for subclasses of {@link AbstractLayer} at some
 * point.
 *
 * @author Nils Bühner
 * @see AbstractSecuredPersistentObjectService
 *
 */
@Service("abstractLayerService")
public class AbstractLayerService<E extends AbstractLayer, D extends AbstractLayerDao<E>>
		extends AbstractSecuredPersistentObjectService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public AbstractLayerService() {
		this((Class<E>) AbstractLayer.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected AbstractLayerService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("abstractLayerDao")
	public void setDao(D dao) {
		this.dao = dao;
	}

	/**
	 *
	 */
	@Autowired
	@Qualifier("mapService")
	MapService<Map, MapDao<Map>> mapService;

	/**
	 *
	 */
	@Override
	@PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#layer, 'DELETE')")
	public void delete (E layer) {
		// get all maps that contain the layer
		Set<Map> maps = mapService.findMapsWithLayer(layer);

		LOG.info("Found " + maps.size() + " maps with layer " + layer);

		// remove the layer from these maps
		for (Map map : maps) {
			map.getMapLayers().remove(layer);
			mapService.saveOrUpdate(map);

			LOG.info("Removed layer from map");
		}

		// finally remove the layer
		super.delete(layer);
	}

}
