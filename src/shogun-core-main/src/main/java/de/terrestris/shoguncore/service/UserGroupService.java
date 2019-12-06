package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.UserGroupDao;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service class for the {@link UserGroup} model.
 *
 * @author Nils Bühner
 * @author Johannes Weskamm
 * @see AbstractCrudService
 */
@Service("userGroupService")
public class UserGroupService<E extends UserGroup, D extends UserGroupDao<E>>
    extends PermissionAwareCrudService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public UserGroupService() {
        this((Class<E>) UserGroup.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected UserGroupService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("userGroupDao")
    public void setDao(D dao) {
        this.dao = dao;
    }

    /**
     * @param groupId
     * @return
     * @throws Exception
     */
    @PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public Set<User> getUsersOfGroup(Integer groupId) throws Exception {
        UserGroup userGroup = this.findById(groupId);
        if (userGroup != null) {
            logger.trace("Found group with ID " + userGroup.getId());
            Set<User> groupUsersSet = userGroup.getMembers();
            return groupUsersSet;
        } else {
            throw new Exception("The group with id " + groupId + " could not be found");
        }
    }
}
