package de.terrestris.shogun2.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.UserGroupDao;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;

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

        Set<User> groupUsersSet = new HashSet<User>();
        UserGroup userGroup = this.findById(groupId);
        if (userGroup != null) {
            LOG.trace("Found group with ID " + userGroup.getId());
            groupUsersSet = userGroup.getMembers();
        } else {
            throw new Exception("The group with id " + groupId + " could not be found");
        }

        return groupUsersSet;
    }

}
