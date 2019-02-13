package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.MapControlDao;
import de.terrestris.shogun2.model.map.MapControl;
import de.terrestris.shogun2.model.module.Module;

/**
 * Service class for the {@link Module} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("mapControlService")
public class MapControlService<E extends MapControl, D extends MapControlDao<E>> extends
    PermissionAwareCrudService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public MapControlService() {
        this((Class<E>) MapControl.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected MapControlService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("mapControlDao")
    public void setDao(D dao) {
        this.dao = dao;
    }
}
