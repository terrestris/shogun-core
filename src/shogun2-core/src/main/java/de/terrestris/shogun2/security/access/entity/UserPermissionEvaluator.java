package de.terrestris.shogun2.security.access.entity;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.security.Permission;

/**
 * @author Nils Bühner
 *
 */
public class UserPermissionEvaluator<E extends User> extends
		PersistentObjectPermissionEvaluator<E> {

	/**
	 * Default constructor
	 */
	@SuppressWarnings("unchecked")
	public UserPermissionEvaluator() {
		this((Class<E>) User.class);
	}

	/**
	 * Constructor for subclasses
	 *
	 * @param entityClass
	 */
	protected UserPermissionEvaluator(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * Grants READ permission on the user object of the currently logged in
	 * user. Uses default implementation otherwise.
	 */
	@Override
	public boolean hasPermission(User user, E entity, Permission permission) {

		// always grant READ access to own user object (of the logged in user)
		if (user != null && user.equals(entity)
				&& permission.equals(Permission.READ)) {
			LOG.trace("Granting READ access on own user object");
			return true;
		}

		// call parent implementation
		return super.hasPermission(user, entity, permission);
	}

}
