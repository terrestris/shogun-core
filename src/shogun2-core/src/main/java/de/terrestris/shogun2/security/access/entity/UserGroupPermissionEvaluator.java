package de.terrestris.shogun2.security.access.entity;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.model.security.Permission;

/**
 * @author Nils Bühner
 *
 */
public class UserGroupPermissionEvaluator<E extends UserGroup> extends
		PersistentObjectPermissionEvaluator<E> {

	/**
	 * Default constructor
	 */
	@SuppressWarnings("unchecked")
	public UserGroupPermissionEvaluator() {
		this((Class<E>) UserGroup.class);
	}

	/**
	 * Constructor for subclasses
	 *
	 * @param entityClass
	 */
	protected UserGroupPermissionEvaluator(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * Grants READ permission on groups where the user is a member.
	 * Uses default implementation otherwise.
	 */
	@Override
	public boolean hasPermission(Integer userId, E userGroup, Permission permission) {

		// always grant READ access to groups in which the user itself is a member
		if (userId != null && permission.equals(Permission.READ)) {

			for (User member : userGroup.getMembers()) {
				if(userId.equals(member.getId())) {
					LOG.trace("Granting READ access on group where the user is member.");
					return true;
				}
			}
		}

		// call parent implementation
		return super.hasPermission(userId, userGroup, permission);
	}

}
