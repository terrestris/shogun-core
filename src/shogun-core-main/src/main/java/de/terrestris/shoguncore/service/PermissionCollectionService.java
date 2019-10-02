package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.PermissionCollectionDao;
import de.terrestris.shoguncore.model.security.PermissionCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service class for the {@link PermissionCollection} model.
 *
 * @author Nils Bühner
 * @author Johannes Weskamm
 * @see AbstractCrudService
 */
@Service("permissionCollectionService")
public class PermissionCollectionService<E extends PermissionCollection, D extends PermissionCollectionDao<E>> extends
    AbstractCrudService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public PermissionCollectionService() {
        this((Class<E>) PermissionCollection.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected PermissionCollectionService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("permissionCollectionDao")
    public void setDao(D dao) {
        this.dao = dao;
    }
}
