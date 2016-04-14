package de.terrestris.shogun2.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.dao.LayerDao;
import de.terrestris.shogun2.model.layer.Layer;
import de.terrestris.shogun2.service.LayerService;

/**
 *
 * @author Nils Buehner
 *
 */
public class LayerIdResolver<E extends Layer, D extends LayerDao<E>, S extends LayerService<E, D>> extends
		PersistentObjectIdResolver<E, D, S> {

	@Override
	@Autowired
	@Qualifier("layerService")
	public void setService(S service) {
		this.service = service;
	}

}
