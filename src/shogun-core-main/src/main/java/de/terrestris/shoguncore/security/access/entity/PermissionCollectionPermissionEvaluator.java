package de.terrestris.shoguncore.security.access.entity;

import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.Permission;
import de.terrestris.shoguncore.model.security.PermissionCollection;

/**
 * @author Nils Bühner
 */
public class PermissionCollectionPermissionEvaluator<E extends PermissionCollection> extends
    PersistentObjectPermissionEvaluator<E> {

    /**
     * Default constructor
     */
    @SuppressWarnings("unchecked")
    public PermissionCollectionPermissionEvaluator() {
        this((Class<E>) PermissionCollection.class);
    }

    /**
     * Constructor for subclasses
     *
     * @param entityClass
     */
    protected PermissionCollectionPermissionEvaluator(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * Always grants every permission on permission collections.
     */
    @Override
    public boolean hasPermission(User user, E permissionCollection, Permission permission) {

        // it is necessary to grant every permission.
        // otherwise permission collections could not be created or updated.
        return true;
    }

}
