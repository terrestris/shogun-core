package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.security.PermissionCollection;
import org.springframework.stereotype.Repository;

@Repository("permissionCollectionDao")
public class PermissionCollectionDao<E extends PermissionCollection> extends GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public PermissionCollectionDao() {
        super((Class<E>) PermissionCollection.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected PermissionCollectionDao(Class<E> clazz) {
        super(clazz);
    }

}
