package de.terrestris.shoguncore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shoguncore.dao.WpsPrimitiveDao;
import de.terrestris.shoguncore.model.wps.WpsPrimitive;

/**
 * Service class for the {@link WpsPrimitive} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("wpsPrimitiveService")
public class WpsPrimitiveService<E extends WpsPrimitive, D extends WpsPrimitiveDao<E>> extends
    WpsParameterService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public WpsPrimitiveService() {
        this((Class<E>) WpsPrimitive.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected WpsPrimitiveService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("wpsPrimitiveDao")
    public void setDao(D dao) {
        this.dao = dao;
    }
}
