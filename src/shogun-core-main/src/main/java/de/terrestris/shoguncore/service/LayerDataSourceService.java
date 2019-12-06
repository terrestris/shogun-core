package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.LayerDataSourceDao;
import de.terrestris.shoguncore.model.layer.source.LayerDataSource;
import de.terrestris.shoguncore.model.module.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service class for the {@link Module} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("layerDataSourceService")
public class LayerDataSourceService<E extends LayerDataSource, D extends LayerDataSourceDao<E>> extends
    PermissionAwareCrudService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public LayerDataSourceService() {
        this((Class<E>) LayerDataSource.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected LayerDataSourceService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("layerDataSourceDao")
    public void setDao(D dao) {
        this.dao = dao;
    }
}
