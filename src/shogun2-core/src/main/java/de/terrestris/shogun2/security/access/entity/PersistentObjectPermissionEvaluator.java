package de.terrestris.shogun2.security.access.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.SecuredPersistentObject;
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
	public boolean hasPermission(Integer userId, E entity, Permission permission) {

		final boolean isSecuredObject = SecuredPersistentObject.class.isAssignableFrom(entityClass);
		final String simpleClassName = entityClass.getSimpleName();

		// HANDLE SECURED OBJECTS
		if(isSecuredObject) {
			SecuredPersistentObject securedObject = (SecuredPersistentObject) entity;

			// CHECK USER PERMISSIONS
			final Map<User, PermissionCollection> userPermissionsMap = securedObject.getUserPermissions();

			PermissionCollection userPermissionCol = extractUserPermissions(userId, userPermissionsMap);
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
			final Map<UserGroup, PermissionCollection> groupPermissionsMap = securedObject.getGroupPermissions();

			PermissionCollection groupPermissionsCol = extractGroupPermissions(userId, groupPermissionsMap);
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

		// HANDLE UNSECURED OBJECTS
		if(permission.equals(Permission.READ)) {
			LOG.trace("Granting READ access on unsecured object '"
					+ simpleClassName + "' with ID " + entity.getId());
			return true;
		} else {
			LOG.trace("Restricting " + permission + " access on unsecured object '"
					+ simpleClassName + "' with ID " + entity.getId());
			return false;
		}

	}

	/**
	 *
	 * @param userId
	 * @param userPermissionsMap
	 * @return
	 */
	protected static PermissionCollection extractUserPermissions(Integer userId,
			Map<User, PermissionCollection> userPermissionsMap) {
		Set<User> users = userPermissionsMap.keySet();
		for(User user : users) {
			if(user.getId().equals(userId)) {
				return userPermissionsMap.get(user);
			}
		}
		// return empty collection
		return new PermissionCollection();
	}

	/**
	 *
	 * @param userId
	 * @param groupPermissionsMap
	 * @return
	 */
	protected static PermissionCollection extractGroupPermissions(Integer userId,
			Map<UserGroup, PermissionCollection> groupPermissionsMap) {

		Set<Permission> aggregatedGroupPermissions = new HashSet<Permission>();

		Set<UserGroup> userGroups = groupPermissionsMap.keySet();

		for(UserGroup userGroup : userGroups) {
			Set<User> groupMembers = userGroup.getMembers();
			for (User user : groupMembers) {
				if(user.getId().equals(userId)) {
					Set<Permission> groupPermissions = groupPermissionsMap.get(userGroup).getPermissions();
					aggregatedGroupPermissions.addAll(groupPermissions);
					break;
				}
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
