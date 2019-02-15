package de.terrestris.shoguncore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shoguncore.dao.MapConfigDao;
import de.terrestris.shoguncore.model.map.MapConfig;
import de.terrestris.shoguncore.model.module.Module;

/**
 * Service class for the {@link Module} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("mapConfigService")
public class MapConfigService<E extends MapConfig, D extends MapConfigDao<E>> extends
    PermissionAwareCrudService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public MapConfigService() {
        this((Class<E>) MapConfig.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected MapConfigService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("mapConfigDao")
    public void setDao(D dao) {
        this.dao = dao;
    }
}
