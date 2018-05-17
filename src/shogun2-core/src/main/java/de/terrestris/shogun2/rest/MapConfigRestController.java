package de.terrestris.shogun2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.MapConfigDao;
import de.terrestris.shogun2.model.map.MapConfig;
import de.terrestris.shogun2.service.MapConfigService;

/**
 * @author Kai Volland
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/mapconfigs")
public class MapConfigRestController<E extends MapConfig, D extends MapConfigDao<E>, S extends MapConfigService<E, D>>
    extends AbstractRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public MapConfigRestController() {
        this((Class<E>) MapConfig.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected MapConfigRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("mapConfigService")
    public void setService(S service) {
        this.service = service;
    }
}
