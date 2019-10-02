package de.terrestris.shoguncore.rest;

import de.terrestris.shoguncore.dao.TileGridDao;
import de.terrestris.shoguncore.model.layer.util.TileGrid;
import de.terrestris.shoguncore.service.TileGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kai Volland
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/tilegrids")
public class TileGridRestController<E extends TileGrid, D extends TileGridDao<E>, S extends TileGridService<E, D>>
    extends AbstractRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public TileGridRestController() {
        this((Class<E>) TileGrid.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected TileGridRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("tileGridService")
    public void setService(S service) {
        this.service = service;
    }
}
