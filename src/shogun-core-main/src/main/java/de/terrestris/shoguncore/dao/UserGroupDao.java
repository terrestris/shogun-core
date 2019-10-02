package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.UserGroup;
import org.springframework.stereotype.Repository;

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
