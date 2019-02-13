package de.terrestris.shoguncore.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shoguncore.model.Role;

@Repository("roleDao")
public class RoleDao<E extends Role> extends GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public RoleDao() {
        super((Class<E>) Role.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected RoleDao(Class<E> clazz) {
        super(clazz);
    }

}
