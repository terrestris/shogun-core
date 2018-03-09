package de.terrestris.shogun2.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.dao.PluginDao;
import de.terrestris.shogun2.model.Plugin;
import de.terrestris.shogun2.service.PluginService;

/**
 * @author Nils Buehner
 */
public class PluginIdResolver<E extends Plugin, D extends PluginDao<E>, S extends PluginService<E, D>> extends
    PersistentObjectIdResolver<E, D, S> {

    @Override
    @Autowired
    @Qualifier("pluginService")
    public void setService(S service) {
        this.service = service;
    }

}
