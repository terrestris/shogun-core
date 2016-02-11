/**
 *
 */
package de.terrestris.shogun2.security.access.entity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.security.Permission;
import de.terrestris.shogun2.util.test.TestUtil;

/**
 * @author Nils Bühner
 *
 */
public class UserPermissionEvaluatorTest extends
	AbstractPersistentObjectPermissionEvaluatorTest<User> {

	public UserPermissionEvaluatorTest() {
		super(User.class, new UserPermissionEvaluator<>(), new User());
	}

	@Test
	public void hasPermission_shouldAlwaysGrantReadOnOwnUserObject() throws NoSuchFieldException, IllegalAccessException {
		Permission readPermission = Permission.READ;

		// prepare a user that
		User user = new User();
		final int userId = 42;
		TestUtil.setIdOnPersistentObject(user, userId);

		// we do not add any permissions to the user, but expect that he is allowed to READ himself
		// call method to test
		boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(userId , user, readPermission);

		assertThat(permissionResult, equalTo(true));

	}

	@Test
	public void hasPermission_shouldNeverGrantAdminDeleteOrWriteOnOwnUserObject() throws NoSuchFieldException, IllegalAccessException {

		// prepare a user that
		User user = new User();
		final int userId = 42;
		TestUtil.setIdOnPersistentObject(user, userId);

		Set<Permission> permissions = new HashSet<Permission>(Arrays.asList(Permission.values()));
		permissions.remove(Permission.READ); // everything but READ

		for (Permission permission : permissions) {
			// call method to test
			boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(userId , user, permission);

			assertThat(permissionResult, equalTo(false));
		}

	}

}
