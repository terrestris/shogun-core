package de.terrestris.shoguncore.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shoguncore.dao.WpsParameterDao;
import de.terrestris.shoguncore.model.wps.WpsParameter;
import de.terrestris.shoguncore.service.WpsParameterService;

/**
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/wpsparameters")
public class WpsParameterRestController<E extends WpsParameter, D extends WpsParameterDao<E>, S extends WpsParameterService<E, D>>
    extends AbstractRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public WpsParameterRestController() {
        this((Class<E>) WpsParameter.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected WpsParameterRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("wpsParameterService")
    public void setService(S service) {
        this.service = service;
    }
}
