/**
 *
 */
package de.terrestris.shogun2.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.terrestris.shogun2.dao.WpsReferenceDao;
import de.terrestris.shogun2.model.wps.WpsReference;
import de.terrestris.shogun2.service.WpsReferenceService;

/**
 * @author Nils Bühner
 */
@Controller
@RequestMapping("/wpsreferences")
public class WpsReferenceController<E extends WpsReference, D extends WpsReferenceDao<E>, S extends WpsReferenceService<E, D>>
    extends WpsParameterController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public WpsReferenceController() {
        this((Class<E>) WpsReference.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected WpsReferenceController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("wpsReferenceService")
    public void setService(S service) {
        this.service = service;
    }

}
