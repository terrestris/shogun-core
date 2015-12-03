package de.terrestris.shogun2.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.UserGroupDao;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;

/**
 * Service class for the {@link UserGroup} model.
 * 
 * @author Nils Bühner
 * @see AbstractCrudService
 * 
 */
@Service("userGroupService")
public class UserGroupService extends AbstractCrudService<UserGroup> {

	public Set<UserGroup> findGroupsOfUser(User user) {
		return ((UserGroupDao) this.dao).findGroupsOfUser(user);
	}

}
