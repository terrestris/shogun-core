package de.terrestris.shogun2.security.access.entity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.SecuredPersistentObject;
import de.terrestris.shogun2.model.security.Permission;

/**
 * @author Nils Bühner
 *
 */
public abstract class AbstractUnsecuredObjectPermissionEvaluatorTest<E extends PersistentObject> extends
		AbstractPersistentObjectPermissionEvaluatorTest<E> {

	protected AbstractUnsecuredObjectPermissionEvaluatorTest(
			Class<E> entityClass,
			PersistentObjectPermissionEvaluator<E> persistentObjectPermissionEvaluator,
			E entityToCheck) {
		super(entityClass, persistentObjectPermissionEvaluator, entityToCheck);
	}

	/**
	 *
	 */
	@Test
	public void hasPermission_shouldAlwaysGrantRead() {
		final boolean isSecuredObject = SecuredPersistentObject.class.isAssignableFrom(entityClass);

		// test only for unsecured objects
		if(isSecuredObject == true) {
			fail("The object is an instance of SecuredPersistentObject and must not use this abstract test class.");
		} else {
			Permission readPermission = Permission.READ;

			Integer userId = 42;

			// call method to test
			boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(userId , entityToCheck, readPermission);

			assertThat(permissionResult, equalTo(true));
		}
	}

	/**
	 *
	 */
	@Test
	public void hasPermission_shouldNeverGrantAdminDeleteOrWrite() {
		final boolean isSecuredObject = SecuredPersistentObject.class.isAssignableFrom(entityClass);

		// test only for unsecured objects
		if(isSecuredObject == true) {
			fail("The object is an instance of SecuredPersistentObject and must not use this abstract test class.");
		} else {
			Set<Permission> permissions = new HashSet<Permission>(Arrays.asList(Permission.values()));
			permissions.remove(Permission.READ); // everything but READ

			// assert that these permissions will never be granted on unsecured objects
			for (Permission permission : permissions) {
				Integer userId = 42;

				// call method to test
				boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(userId , entityToCheck, permission);

				assertThat(permissionResult, equalTo(false));
			}
		}
	}

}
