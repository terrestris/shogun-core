package de.terrestris.shoguncore.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shoguncore.dao.ButtonDao;
import de.terrestris.shoguncore.model.module.Button;
import de.terrestris.shoguncore.service.ButtonService;

/**
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/buttons")
public class ButtonRestController<E extends Button, D extends ButtonDao<E>, S extends ButtonService<E, D>>
    extends AbstractRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public ButtonRestController() {
        this((Class<E>) Button.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected ButtonRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("buttonService")
    public void setService(S service) {
        this.service = service;
    }
}
