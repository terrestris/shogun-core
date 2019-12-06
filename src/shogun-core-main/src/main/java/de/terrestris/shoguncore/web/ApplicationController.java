package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.dao.ApplicationDao;
import de.terrestris.shoguncore.model.Application;
import de.terrestris.shoguncore.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Nils Bühner
 */
@Controller
@RequestMapping("/application")
public class ApplicationController<E extends Application, D extends ApplicationDao<E>, S extends ApplicationService<E, D>>
    extends AbstractWebController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public ApplicationController() {
        this((Class<E>) Application.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected ApplicationController(Class<E> entityClass) {
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

    @RequestMapping(value = "/findAll.action", method = RequestMethod.GET)
    public @ResponseBody
    List<E> findAllApplications() {
        logger.info("Trying to find all Applications.");

        return service.findAll();
    }

}
