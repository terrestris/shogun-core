package de.terrestris.shoguncore.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shoguncore.dao.LayerDao;
import de.terrestris.shoguncore.model.layer.Layer;
import de.terrestris.shoguncore.service.LayerService;

/**
 * @author Nils Buehner
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
