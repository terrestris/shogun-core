package de.terrestris.shoguncore.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shoguncore.model.UserGroup;

@Repository("userGroupDao")
public class UserGroupDao<E extends UserGroup> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public UserGroupDao() {
        super((Class<E>) UserGroup.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected UserGroupDao(Class<E> clazz) {
        super(clazz);
    }

}
