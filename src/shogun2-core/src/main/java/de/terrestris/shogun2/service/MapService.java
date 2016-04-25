package de.terrestris.shogun2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.AbstractLayerDao;
import de.terrestris.shogun2.dao.MapDao;
import de.terrestris.shogun2.model.PersistentObject;
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

	@Autowired
	@Qualifier("abstractLayerService")
	private AbstractLayerService<AbstractLayer, AbstractLayerDao<AbstractLayer>> abstractLayerService;

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
	@PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#e, 'READ')")
	public Set<E> findMapsWithLayer(AbstractLayer layer) {
		return dao.findMapsWithLayer(layer);
	}

	/**
	 * TODO secure this method!?
	 *
	 * @param MapModuleId
	 * @param abstractLayerIds
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AbstractLayer> setLayersForMap (Integer mapModuleId, List<Integer> abstractLayerIds) throws Exception{
		E module = this.findById(mapModuleId);
		List<AbstractLayer> layers = new ArrayList<AbstractLayer>();

		if(module instanceof Map) {
			Map mapModule = (Map) module;
			for (Integer id : abstractLayerIds) {
				PersistentObject entity = this.abstractLayerService.findById(id);
				if(entity instanceof AbstractLayer){
					AbstractLayer layer = (AbstractLayer) entity;
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
