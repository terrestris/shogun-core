/**
 *
 */
package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.dao.WpsPluginDao;
import de.terrestris.shoguncore.model.wps.WpsPlugin;
import de.terrestris.shoguncore.service.WpsPluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Nils Bühner
 */
@Controller
@RequestMapping("/wpsplugins")
public class WpsPluginController<E extends WpsPlugin, D extends WpsPluginDao<E>, S extends WpsPluginService<E, D>>
    extends PluginController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public WpsPluginController() {
        this((Class<E>) WpsPlugin.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected WpsPluginController(Class<E> entityClass) {
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
