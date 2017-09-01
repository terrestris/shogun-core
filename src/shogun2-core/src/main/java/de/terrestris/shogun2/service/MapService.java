package de.terrestris.shogun2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.LayerDao;
import de.terrestris.shogun2.dao.MapDao;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.layer.Layer;
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

	@Autowired
	@Qualifier("layerService")
	private LayerService<Layer, LayerDao<Layer>> layerService;

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
	@PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#layer, 'READ')")
	@Transactional(readOnly = true)
	public Set<E> findMapsWithLayer(Layer layer) {
		return dao.findMapsWithLayer(layer);
	}

	/**
	 * TODO secure this method!?
	 *
	 * @param MapModuleId
	 * @param layerIds
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#mapModuleId, 'de.terrestris.shogun2.model.module.Map', 'UPDATE')")
	public List<Layer> setLayersForMap (Integer mapModuleId, List<Integer> layerIds) throws Exception{
		E module = this.findById(mapModuleId);
		List<Layer> layers = new ArrayList<Layer>();

		if(module instanceof Map) {
			Map mapModule = (Map) module;
			for (Integer id : layerIds) {
				PersistentObject entity = this.layerService.findById(id);
				if(entity instanceof Layer){
					Layer layer = (Layer) entity;
					if(layer != null){
						layers.add(layer);
					}
				}
			}
			mapModule.setMapLayers(layers);
			this.saveOrUpdate((E) mapModule);
			return layers;
		} else {
			throw new Exception("Can't find mapModule with id " + mapModuleId.toString());
		}

	}
}
