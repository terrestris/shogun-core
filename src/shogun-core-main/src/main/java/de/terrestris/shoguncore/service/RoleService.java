package de.terrestris.shoguncore.service;

import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shoguncore.dao.RoleDao;
import de.terrestris.shoguncore.model.Role;

/**
 * Service class for the {@link Role} model.
 *
 * @author Nils Bühner
 */
@Service("roleService")
public class RoleService<E extends Role, D extends RoleDao<E>> extends
    PermissionAwareCrudService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public RoleService() {
        this((Class<E>) Role.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected RoleService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * Returns the role for the given (unique) role name.
     * If no role was found, null will be returned.
     *
     * @param roleName A unique role name.
     * @return The unique role for the role name or null.
     */
    @Transactional(readOnly = true)
    public E findByRoleName(String roleName) {

        SimpleExpression eqRoleName =
            Restrictions.eq("name", roleName);

        E role = dao.findByUniqueCriteria(eqRoleName);

        return role;
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("roleDao")
    public void setDao(D dao) {
        this.dao = dao;
    }

}
