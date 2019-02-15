package de.terrestris.shoguncore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shoguncore.dao.WpsParameterDao;
import de.terrestris.shoguncore.model.wps.WpsParameter;

/**
 * Service class for the {@link WpsParameter} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("wpsParameterService")
public class WpsParameterService<E extends WpsParameter, D extends WpsParameterDao<E>> extends
    PermissionAwareCrudService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public WpsParameterService() {
        this((Class<E>) WpsParameter.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected WpsParameterService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("wpsParameterDao")
    public void setDao(D dao) {
        this.dao = dao;
    }
}
