package de.terrestris.shoguncore.rest;

import de.terrestris.shoguncore.dao.LayerAppearanceDao;
import de.terrestris.shoguncore.model.layer.appearance.LayerAppearance;
import de.terrestris.shoguncore.service.LayerAppearanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kai Volland
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/layerappearances")
public class LayerAppearanceRestController<E extends LayerAppearance, D extends LayerAppearanceDao<E>, S extends LayerAppearanceService<E, D>>
    extends AbstractRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public LayerAppearanceRestController() {
        this((Class<E>) LayerAppearance.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected LayerAppearanceRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("layerAppearanceService")
    public void setService(S service) {
        this.service = service;
    }
}
