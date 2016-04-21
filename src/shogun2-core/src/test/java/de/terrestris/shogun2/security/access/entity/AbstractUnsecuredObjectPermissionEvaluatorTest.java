package de.terrestris.shogun2.security.access.entity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.terrestris.shogun2.helper.IdHelper;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.SecuredPersistentObject;
import de.terrestris.shogun2.model.User;
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
	 * @throws IllegalAccessException 
	 * @throws NoSuchFieldException 
	 *
	 */
	@Test
	public void hasPermission_shouldAlwaysGrantRead() throws NoSuchFieldException, IllegalAccessException {
		final boolean isSecuredObject = SecuredPersistentObject.class.isAssignableFrom(entityClass);

		// test only for unsecured objects
		if(isSecuredObject == true) {
			fail("The object is an instance of SecuredPersistentObject and must not use this abstract test class.");
		} else {
			Permission readPermission = Permission.READ;

			final User user = new User("First name", "Last Name", "accountName");
			IdHelper.setIdOnPersistentObject(user, 42);

			// call method to test
			boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user , entityToCheck, readPermission);

			assertThat(permissionResult, equalTo(true));
		}
	}

	/**
	 * @throws IllegalAccessException 
	 * @throws NoSuchFieldException 
	 *
	 */
	@Test
	public void hasPermission_shouldNeverGrantAdminDeleteOrWrite() throws NoSuchFieldException, IllegalAccessException {
		final boolean isSecuredObject = SecuredPersistentObject.class.isAssignableFrom(entityClass);

		// test only for unsecured objects
		if(isSecuredObject == true) {
			fail("The object is an instance of SecuredPersistentObject and must not use this abstract test class.");
		} else {
			Set<Permission> permissions = new HashSet<Permission>(Arrays.asList(Permission.values()));
			permissions.remove(Permission.READ); // everything but READ

			// assert that these permissions will never be granted on unsecured objects
			for (Permission permission : permissions) {
				final User user = new User("First name", "Last Name", "accountName");
				IdHelper.setIdOnPersistentObject(user, 42);

				// call method to test
				boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user, entityToCheck, permission);

				assertThat(permissionResult, equalTo(false));
			}
		}
	}

}
