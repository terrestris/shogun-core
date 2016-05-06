package de.terrestris.shogun2.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.dao.LayerGroupDao;
import de.terrestris.shogun2.model.layer.LayerGroup;
import de.terrestris.shogun2.service.LayerGroupService;

/**
 *
 * @author Kai Volland
 *
 */
public class LayerGroupIdResolver<E extends LayerGroup, D extends LayerGroupDao<E>, S extends LayerGroupService<E, D>> extends
		PersistentObjectIdResolver<E, D, S> {

	@Override
	@Autowired
	@Qualifier("layerGroupService")
	public void setService(S service) {
		this.service = service;
	}

}
