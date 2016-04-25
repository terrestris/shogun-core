package de.terrestris.shogun2.security.access.factory;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.security.access.entity.PersistentObjectPermissionEvaluator;
import de.terrestris.shogun2.security.access.entity.UserGroupPermissionEvaluator;
import de.terrestris.shogun2.security.access.entity.UserPermissionEvaluator;


/**
 * @author Nils Bühner
 *
 */
public class EntityPermissionEvaluatorFactory<E extends PersistentObject> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PersistentObjectPermissionEvaluator<E> getEntityPermissionEvaluator(
			final Class<E> entityClass) {

		if(User.class.isAssignableFrom(entityClass)) {
			return new UserPermissionEvaluator();
		}

		if(UserGroup.class.isAssignableFrom(entityClass)) {
			return new UserGroupPermissionEvaluator();
		}

		// fall back on default implementation
		return new PersistentObjectPermissionEvaluator<E>(entityClass);

	}

}
