package de.terrestris.shogun2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.LayerDataSourceDao;
import de.terrestris.shogun2.model.layer.source.LayerDataSource;
import de.terrestris.shogun2.service.LayerDataSourceService;

/**
 * @author Kai Volland
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/layerdatasources")
public class LayerDataSourceRestController<E extends LayerDataSource, D extends LayerDataSourceDao<E>, S extends LayerDataSourceService<E, D>>
    extends AbstractRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public LayerDataSourceRestController() {
        this((Class<E>) LayerDataSource.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected LayerDataSourceRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("layerDataSourceService")
    public void setService(S service) {
        this.service = service;
    }
}
