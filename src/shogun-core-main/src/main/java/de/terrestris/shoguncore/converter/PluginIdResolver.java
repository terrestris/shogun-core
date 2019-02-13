package de.terrestris.shoguncore.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shoguncore.dao.PluginDao;
import de.terrestris.shoguncore.model.Plugin;
import de.terrestris.shoguncore.service.PluginService;

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
