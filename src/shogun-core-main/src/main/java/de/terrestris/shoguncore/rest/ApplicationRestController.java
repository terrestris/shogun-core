package de.terrestris.shoguncore.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shoguncore.dao.ApplicationDao;
import de.terrestris.shoguncore.model.Application;
import de.terrestris.shoguncore.service.ApplicationService;

/**
 * @author Kai Volland
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/applications")
public class ApplicationRestController<E extends Application, D extends ApplicationDao<E>, S extends ApplicationService<E, D>>
    extends AbstractRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public ApplicationRestController() {
        this((Class<E>) Application.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected ApplicationRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("applicationService")
    public void setService(S service) {
        this.service = service;
    }

}
