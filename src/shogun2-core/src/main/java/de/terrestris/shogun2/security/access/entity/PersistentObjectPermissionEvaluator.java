package de.terrestris.shogun2.security.access.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.model.security.Permission;
import de.terrestris.shogun2.model.security.PermissionCollection;

/**
 * @author Nils Bühner
 * @author Johannes Weskamm
 *
 */
public class PersistentObjectPermissionEvaluator<E extends PersistentObject> {

	/**
	 * The LOGGER instance
	 */
	protected final Logger LOG = Logger.getLogger(getClass());

	/**
	 * Represents the class of the entity
	 */
	private final Class<E> entityClass;

	/**
	 *
	 * @param entityClass
	 */
	public PersistentObjectPermissionEvaluator(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 *
	 * @param userId
	 * @param entity
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(User user, E entity, Permission permission) {

		final String simpleClassName = entityClass.getSimpleName();

		// CHECK USER PERMISSIONS
		final Map<User, PermissionCollection> userPermissionsMap = entity.getUserPermissions();

		PermissionCollection userPermissionCol = extractUserPermissions(user, userPermissionsMap);
		final Set<Permission> userPermissions = userPermissionCol.getPermissions();

		// grant access if user explicitly has the requested permission or
		// if the user has the ADMIN permission
		if (userPermissions.contains(permission)
				|| userPermissions.contains(Permission.ADMIN)) {
			LOG.trace("Granting " + permission
					+ " access by user permissions");
			return true;
		}

		// CHECK GROUP PERMISSIONS
		final Map<UserGroup, PermissionCollection> groupPermissionsMap = entity.getGroupPermissions();

		PermissionCollection groupPermissionsCol = extractGroupPermissions(user, groupPermissionsMap);
		final Set<Permission> groupPermissions = groupPermissionsCol.getPermissions();

		// grant access if group explicitly has the requested permission or
		// if the group has the ADMIN permission
		if (groupPermissions.contains(permission)
				|| groupPermissions.contains(Permission.ADMIN)) {
			LOG.trace("Granting " + permission
					+ " access by group permissions");
			return true;
		}

		LOG.trace("Restricting " + permission + " access on secured object '"
				+ simpleClassName + "' with ID " + entity.getId());
		return false;
	}

	/**
	 *
	 * @param userId
	 * @param userPermissionsMap
	 * @return
	 */
	protected static PermissionCollection extractUserPermissions(User user,
			Map<User, PermissionCollection> userPermissionsMap) {

		PermissionCollection permissionCollection = userPermissionsMap.get(user);

		if(permissionCollection == null) {
			// return empty collection
			permissionCollection = new PermissionCollection();
		}

		return permissionCollection;
	}

	/**
	 *
	 * @param userId
	 * @param groupPermissionsMap
	 * @return
	 */
	protected static PermissionCollection extractGroupPermissions(User user,
			Map<UserGroup, PermissionCollection> groupPermissionsMap) {

		Set<Permission> aggregatedGroupPermissions = new HashSet<Permission>();

		Set<UserGroup> userGroupsWithPermissions = groupPermissionsMap.keySet();

		for(UserGroup userGroup : userGroupsWithPermissions) {

			if(userGroup.getMembers().contains(user)) {
				Set<Permission> groupPermissions = groupPermissionsMap.get(userGroup).getPermissions();
				aggregatedGroupPermissions.addAll(groupPermissions);
			}

		}

		return new PermissionCollection(aggregatedGroupPermissions);
	}

	/**
	 * @return the entityClass
	 */
	public Class<E> getEntityClass() {
		return entityClass;
	}

}
