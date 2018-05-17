package de.terrestris.shogun2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.WpsPluginDao;
import de.terrestris.shogun2.model.wps.WpsPlugin;
import de.terrestris.shogun2.service.WpsPluginService;

/**
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/wpsplugins")
public class WpsPluginRestController<E extends WpsPlugin, D extends WpsPluginDao<E>, S extends WpsPluginService<E, D>>
    extends PluginRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public WpsPluginRestController() {
        this((Class<E>) WpsPlugin.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected WpsPluginRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("wpsPluginService")
    public void setService(S service) {
        this.service = service;
    }
}
