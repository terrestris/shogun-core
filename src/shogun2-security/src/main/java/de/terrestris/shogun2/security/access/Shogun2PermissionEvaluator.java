package de.terrestris.shogun2.security.access;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.security.Permission;
import de.terrestris.shogun2.security.access.entity.PersistentObjectPermissionEvaluator;
import de.terrestris.shogun2.security.access.factory.EntityPermissionEvaluatorFactory;

/**
 * @author Nils Bühner
 *
 */
public class Shogun2PermissionEvaluator implements PermissionEvaluator {

	/**
	 * The LOGGER instance
	 */
	private final static Logger LOG = Logger.getLogger(Shogun2PermissionEvaluator.class);

	@SuppressWarnings("rawtypes")
	private EntityPermissionEvaluatorFactory permissionEvaluatorFactory;

	/**
	 *
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean hasPermission(Authentication authentication,
			Object targetDomainObject, Object permissionObject) {

		boolean hasPermission = false;

		if (authentication != null
				&& authentication.getPrincipal() instanceof User
				&& targetDomainObject != null
				&& targetDomainObject instanceof PersistentObject
				&& permissionObject instanceof String) {

			final User user = (User) authentication.getPrincipal();
			final PersistentObject persistentObject = (PersistentObject) targetDomainObject;
			final Integer objectId = persistentObject.getId();
			final String simpleClassName = targetDomainObject.getClass().getSimpleName();
			final Permission permission = Permission.fromString((String) permissionObject);

			LOG.trace("Evaluating whether user '" + user.getAccountName()
					+ "' has permission '" + permission + "' on '"
					+ simpleClassName + "' with ID " + objectId);

			PersistentObjectPermissionEvaluator entityPermissionEvaluator = permissionEvaluatorFactory
					.getEntityPermissionEvaluator(persistentObject.getClass());

			hasPermission = entityPermissionEvaluator.hasPermission(user.getId(), persistentObject, permission);

		} else {
			LOG.error("Permission evaluation has been aborted.");
		}

		return hasPermission;
	}

	/**
	 * We do currently do not support/use this implementation.
	 */
	@Override
	public boolean hasPermission(Authentication authentication,
			Serializable targetId, String targetType, Object permission) {
		return false;
	}

	/**
	 * @return the permissionEvaluatorFactory
	 */
	@SuppressWarnings("rawtypes")
	public EntityPermissionEvaluatorFactory getPermissionEvaluatorFactory() {
		return permissionEvaluatorFactory;
	}

	/**
	 * @param permissionEvaluatorFactory the permissionEvaluatorFactory to set
	 */
	@SuppressWarnings("rawtypes")
	public void setPermissionEvaluatorFactory(
			EntityPermissionEvaluatorFactory permissionEvaluatorFactory) {
		this.permissionEvaluatorFactory = permissionEvaluatorFactory;
	}

}
