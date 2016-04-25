package de.terrestris.shogun2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.AbstractLayerDao;
import de.terrestris.shogun2.dao.LayerGroupDao;
import de.terrestris.shogun2.model.layer.AbstractLayer;
import de.terrestris.shogun2.model.layer.LayerGroup;
import de.terrestris.shogun2.model.module.Module;

/**
 * Service class for the {@link Module} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("layerGroupService")
public class LayerGroupService<E extends LayerGroup, D extends LayerGroupDao<E>> extends
		AbstractLayerService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public LayerGroupService() {
		this((Class<E>) LayerGroup.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected LayerGroupService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("layerGroupDao")
	public void setDao(D dao) {
		this.dao = dao;
	}

	/**
	 * We need this service for the {@link AbstractLayer}s to retrieve
	 * all subtypes of {@link AbstractLayer} from here (but not only from
	 * the {@link LayerGroup} hierarchy.
	 */
	@Autowired
	@Qualifier("abstractLayerService")
	private AbstractLayerService<AbstractLayer, AbstractLayerDao<AbstractLayer>> abstractLayerService;

	/**
	 *
	 * @param abstractLayer
	 * @return
	 */
	@PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(returnObject, 'READ')")
	public Set<E> findLayerGroupsOfAbstractLayer(Integer abstractLayerId) {
		return dao.findLayerGroupsOfAbstractLayer(abstractLayerId);
	}

	/**
	 * TODO secure this method !!?
	 *
	 * @param layerGroupId
	 * @param abstractLayerIds
	 * @return
	 * @throws Exception
	 */
	public List<AbstractLayer> setLayersForLayerGroup (Integer layerGroupId, List<Integer> abstractLayerIds) {
		E layerGroup = this.findById(layerGroupId);
		List<AbstractLayer> layers = new ArrayList<AbstractLayer>();

		for (Integer id : abstractLayerIds) {
			// this call may throw an AccessDeniedException !?
			// maybe we should catch and ignore this here?
			AbstractLayer layer = abstractLayerService.findById(id);
			if(layer != null){
				layers.add(layer);
			}
		}

		layerGroup.setLayers(layers);

		this.saveOrUpdate(layerGroup);

		return layers;
	}
}
