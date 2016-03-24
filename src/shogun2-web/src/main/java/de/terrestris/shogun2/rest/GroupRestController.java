package de.terrestris.shogun2.rest;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.UserGroupDao;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.service.UserGroupService;
import de.terrestris.shogun2.util.data.ResultSet;

/**
 * @author Johannes Weskamm
 * @author Nils Bühner
 *
 */
@RestController
@RequestMapping("/groups")
public class GroupRestController<E extends UserGroup, D extends UserGroupDao<E>, S extends UserGroupService<E, D>>
		extends AbstractRestController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public GroupRestController() {
		this((Class<E>) UserGroup.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected GroupRestController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("userGroupService")
	public void setService(S service) {
		this.service = service;
	}

	/**
	 * Get the users of a specific group.
	 *
	 * @param groupId
	 * @return
	 */
	@RequestMapping(value = "/{groupId}/users", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> findUsersOfGroup(@PathVariable Integer groupId) {

		try {
			Set<User> groupUsersSet = this.service.getUsersOfGroup(groupId);
			return ResultSet.success(groupUsersSet);
		} catch (Exception e) {
			LOG.error("Error finding group with id " + groupId + ": "
					+ e.getMessage());
			return ResultSet.error("Error finding group with id " + groupId);
		}
	}
}
