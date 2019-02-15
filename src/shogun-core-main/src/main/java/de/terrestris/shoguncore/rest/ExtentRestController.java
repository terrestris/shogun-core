package de.terrestris.shoguncore.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shoguncore.dao.ExtentDao;
import de.terrestris.shoguncore.model.layer.util.Extent;
import de.terrestris.shoguncore.service.ExtentService;

/**
 * @author Kai Volland
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/extents")
public class ExtentRestController<E extends Extent, D extends ExtentDao<E>, S extends ExtentService<E, D>>
    extends AbstractRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public ExtentRestController() {
        this((Class<E>) Extent.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected ExtentRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("extentService")
    public void setService(S service) {
        this.service = service;
    }
}
