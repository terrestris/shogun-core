package de.terrestris.shogun2.security.access.entity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.terrestris.shogun2.helper.IdHelper;
import de.terrestris.shogun2.model.SecuredPersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.model.security.Permission;
import de.terrestris.shogun2.model.security.PermissionCollection;

/**
 * @author Nils Bühner
 *
 */
public abstract class AbstractSecuredObjectPermissionEvaluatorTest<E extends SecuredPersistentObject> extends
		AbstractPersistentObjectPermissionEvaluatorTest<E> {

	/**
	 * Constructor for subclasses
	 *
	 * @param entityClass
	 * @param persistentObjectPermissionEvaluator
	 * @param entityToCheck
	 */
	protected AbstractSecuredObjectPermissionEvaluatorTest(
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
	public void hasPermission_shouldNeverGrantAnythingWithoutPermissions() throws NoSuchFieldException, IllegalAccessException {
		Set<Permission> allPermissions = new HashSet<Permission>(Arrays.asList(Permission.values()));

		// assert that no permission will ever be granted on secured objects
		// if no permissions are set
		for (Permission permission : allPermissions) {
			final User user = new User("First name", "Last Name", "accountName");
			IdHelper.setIdOnPersistentObject(user, 42);

			// call method to test
			boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user , entityToCheck, permission);

			assertThat(permissionResult, equalTo(false));

		}
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 *
	 */
	@Test
	public void hasPermission_shouldGrantPermissionOnSecuredObjectWithCorrectUserPermission() throws NoSuchFieldException, IllegalAccessException {
		// prepare a user that gets permissions
		final User user = new User("First name", "Last Name", "accountName");
		IdHelper.setIdOnPersistentObject(user, 42);

		// prepare permission collection/map
		Map<User, PermissionCollection> userPermissionsMap = new HashMap<User, PermissionCollection>();

		// WRITE as example permission for the user
		Permission writePermission = Permission.WRITE;
		PermissionCollection permissionCollection = buildPermissionCollection(writePermission);
		userPermissionsMap.put(user , permissionCollection);
		entityToCheck.setUserPermissions(userPermissionsMap);

		// call method to test
		boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user , entityToCheck, writePermission);

		assertThat(permissionResult, equalTo(true));
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 *
	 */
	@Test
	public void hasPermission_shouldGrantPermissionOnSecuredObjectWithCorrectGroupPermission() throws NoSuchFieldException, IllegalAccessException {
		// prepare a user
		final User user = new User("First name", "Last Name", "accountName");
		IdHelper.setIdOnPersistentObject(user, 42);

		// add user to a group
		UserGroup group = new UserGroup();
		group.getMembers().add(user);

		// prepare permission collection/map for the group
		Map<UserGroup, PermissionCollection> userGroupPermissionsMap = new HashMap<UserGroup, PermissionCollection>();

		// WRITE as example permission for the group
		Permission writePermission = Permission.WRITE;
		PermissionCollection permissionCollection = buildPermissionCollection(writePermission);
		userGroupPermissionsMap.put(group , permissionCollection);
		entityToCheck.setGroupPermissions(userGroupPermissionsMap);

		// call method to test
		boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user , entityToCheck, writePermission);

		assertThat(permissionResult, equalTo(true));
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 *
	 */
	@Test
	public void hasPermission_shouldGrantAnyPermissionOnSecuredObjectWithUserAdminPermission() throws NoSuchFieldException, IllegalAccessException {
		final boolean isSecuredObject = SecuredPersistentObject.class.isAssignableFrom(entityClass);

		// test only for secured objects
		if(isSecuredObject == true) {

			// prepare a user that gets permissions
			final User user = new User("First name", "Last Name", "accountName");
			IdHelper.setIdOnPersistentObject(user, 42);

			// prepare permission collection/map
			Map<User, PermissionCollection> userPermissionsMap = new HashMap<User, PermissionCollection>();

			// grant ADMIN permission to user
			Permission adminPermission = Permission.ADMIN;
			PermissionCollection permissionCollection = buildPermissionCollection(adminPermission);
			userPermissionsMap.put(user , permissionCollection);
			entityToCheck.setUserPermissions(userPermissionsMap);

			Set<Permission> allPermissions = new HashSet<Permission>(Arrays.asList(Permission.values()));

			// check that the ADMIN permission allows the user to to everything
			for (Permission permission : allPermissions) {
				// call method to test
				boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user , entityToCheck, permission);

				assertThat(permissionResult, equalTo(true));
			}

		}
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 *
	 */
	@Test
	public void hasPermission_shouldGrantAnyPermissionOnSecuredObjectWithUserGroupAdminPermission() throws NoSuchFieldException, IllegalAccessException {

		// prepare a user
		final User user = new User("First name", "Last Name", "accountName");
		IdHelper.setIdOnPersistentObject(user, 42);

		// add user to group
		UserGroup group = new UserGroup();
		group.getMembers().add(user);

		// prepare permission collection/map for the group
		Map<UserGroup, PermissionCollection> groupPermissionsMap = new HashMap<UserGroup, PermissionCollection>();

		// grant ADMIN permission to group
		Permission adminPermission = Permission.ADMIN;
		PermissionCollection permissionCollection = buildPermissionCollection(adminPermission);
		groupPermissionsMap.put(group , permissionCollection);
		entityToCheck.setGroupPermissions(groupPermissionsMap);

		Set<Permission> allPermissions = new HashSet<Permission>(Arrays.asList(Permission.values()));

		// check that the ADMIN permission allows the user to to everything
		for (Permission permission : allPermissions) {
			// call method to test
			boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user , entityToCheck, permission);

			assertThat(permissionResult, equalTo(true));
		}

	}

	/**
	 * Helper method to easily build a {@link PermissionCollection}
	 *
	 * @param writePermission
	 * @return
	 */
	private PermissionCollection buildPermissionCollection(
			Permission... permissions) {
		return new PermissionCollection(new HashSet<Permission>(Arrays.asList(permissions)));
	}

}
