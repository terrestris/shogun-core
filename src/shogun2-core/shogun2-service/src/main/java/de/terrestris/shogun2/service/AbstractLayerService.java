package de.terrestris.shogun2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.AbstractLayerDao;
import de.terrestris.shogun2.dao.MapDao;
import de.terrestris.shogun2.model.layer.AbstractLayer;
import de.terrestris.shogun2.model.layer.LayerGroup;
import de.terrestris.shogun2.model.module.Map;
import de.terrestris.shogun2.model.module.Module;

/**
 * Service class for the {@link Module} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("abstractLayerService")
public class AbstractLayerService<E extends AbstractLayer, D extends AbstractLayerDao<E>> extends
		AbstractCrudService<E, D> {

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

	@Autowired
	@Qualifier("mapDao")
	MapDao<Map> mapDao;

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
	 * @param abstractLayer
	 * @return
	 */
	public Set<E> findLayerGroupsOfAbstractLayer(AbstractLayer abstractLayer) {
		return dao.findLayerGroupsOfAbstractLayer(abstractLayer);
	}
	
	/**
	 * 
	 * @param layerGroupId
	 * @param abstractLayerIds
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AbstractLayer> setLayersForLayerGroup (Integer layerGroupId, List<Integer> abstractLayerIds) throws Exception{
		E abstractlayer = this.findById(layerGroupId);
		List<AbstractLayer> layers = new ArrayList<AbstractLayer>();

		if(abstractlayer instanceof LayerGroup) {
			LayerGroup layerGroup = (LayerGroup) abstractlayer;
			for (Integer id : abstractLayerIds) {
				AbstractLayer layer = this.findById(id);
				if(layer != null){
					layers.add(layer);
				}
			}
			layerGroup.setLayers(layers);
			this.saveOrUpdate((E) layerGroup);
			return layers;
		} else {
			throw new Exception("Layer of with given Id is not a LayerGroup.");
		}

	}

	@Override
	public void delete (E layer) {
		LOG.info("OVERRIDE DELETE OF LAYER");

		// get all maps that contain the layer
		Set<Map> maps = mapDao.findMapsWithLayer(layer);

		LOG.info("Found " + maps.size() + " maps with layer " + layer);

		// remove the layer from these maps
		for (Map map : maps) {
			map.getMapLayers().remove(layer);
			mapDao.saveOrUpdate(map);

			LOG.info("Removed layer from map");
		}

		// finally remove the layer
		super.delete(layer);
	}
}
