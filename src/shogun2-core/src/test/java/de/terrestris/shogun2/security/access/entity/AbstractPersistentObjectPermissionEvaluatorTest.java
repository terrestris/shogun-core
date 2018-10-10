package de.terrestris.shogun2.security.access.entity;

import de.terrestris.shogun2.helper.IdHelper;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.model.security.Permission;
import de.terrestris.shogun2.model.security.PermissionCollection;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Nils Bühner
 */
public abstract class AbstractPersistentObjectPermissionEvaluatorTest<E extends PersistentObject> {

    // the permission evaluator to test
    protected PersistentObjectPermissionEvaluator<E> persistentObjectPermissionEvaluator;

    protected final Class<E> entityClass;

    protected E entityToCheck;

    /**
     * Constructor that has to be implemented by subclasses
     *
     * @param entityClass
     */
    protected AbstractPersistentObjectPermissionEvaluatorTest(
        Class<E> entityClass,
        PersistentObjectPermissionEvaluator<E> persistentObjectPermissionEvaluator,
        E entityToCheck) {
        this.entityClass = entityClass;
        this.persistentObjectPermissionEvaluator = persistentObjectPermissionEvaluator;
        this.entityToCheck = entityToCheck;
    }

    /**
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
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
            boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user, entityToCheck, permission);

            assertThat(permissionResult, equalTo(false));

        }
    }

    /**
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    @Test
    public void hasPermission_shouldGrantPermissionOnSecuredObjectWithCorrectUserPermission() throws NoSuchFieldException, IllegalAccessException {
        // prepare a user that gets permissions
        final User user = new User("First name", "Last Name", "accountName");
        IdHelper.setIdOnPersistentObject(user, 42);

        // prepare permission collection/map
        Map<User, PermissionCollection> userPermissionsMap = new HashMap<User, PermissionCollection>();

        // UPDATE as example permission for the user
        Permission updatePermission = Permission.UPDATE;
        PermissionCollection permissionCollection = buildPermissionCollection(updatePermission);
        userPermissionsMap.put(user, permissionCollection);
        entityToCheck.setUserPermissions(userPermissionsMap);

        // call method to test
        boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user, entityToCheck, updatePermission);

        assertThat(permissionResult, equalTo(true));
    }

    /**
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
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

        // UPDATE as example permission for the group
        Permission updatePermission = Permission.UPDATE;
        PermissionCollection permissionCollection = buildPermissionCollection(updatePermission);
        userGroupPermissionsMap.put(group, permissionCollection);
        entityToCheck.setGroupPermissions(userGroupPermissionsMap);

        // call method to test
        boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user, entityToCheck, updatePermission);

        assertThat(permissionResult, equalTo(true));
    }

    /**
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    @Test
    public void hasPermission_shouldGrantAnyPermissionOnSecuredObjectWithUserAdminPermission() throws NoSuchFieldException, IllegalAccessException {

        // prepare a user that gets permissions
        final User user = new User("First name", "Last Name", "accountName");
        IdHelper.setIdOnPersistentObject(user, 42);

        // prepare permission collection/map
        Map<User, PermissionCollection> userPermissionsMap = new HashMap<User, PermissionCollection>();

        // grant ADMIN permission to user
        Permission adminPermission = Permission.ADMIN;
        PermissionCollection permissionCollection = buildPermissionCollection(adminPermission);
        userPermissionsMap.put(user, permissionCollection);
        entityToCheck.setUserPermissions(userPermissionsMap);

        Set<Permission> allPermissions = new HashSet<Permission>(Arrays.asList(Permission.values()));

        // check that the ADMIN permission allows the user to to everything
        for (Permission permission : allPermissions) {
            // call method to test
            boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user, entityToCheck, permission);

            assertThat(permissionResult, equalTo(true));
        }

    }

    @Test
    public void testEntityClass() {
        assertEquals(persistentObjectPermissionEvaluator.getEntityClass(), entityClass);
    }

    /**
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
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
        groupPermissionsMap.put(group, permissionCollection);
        entityToCheck.setGroupPermissions(groupPermissionsMap);

        Set<Permission> allPermissions = new HashSet<Permission>(Arrays.asList(Permission.values()));

        // check that the ADMIN permission allows the user to to everything
        for (Permission permission : allPermissions) {
            // call method to test
            boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user, entityToCheck, permission);

            assertThat(permissionResult, equalTo(true));
        }

    }

    /**
     * Helper method to easily build a {@link PermissionCollection}
     *
     * @param permissions
     * @return
     */
    private PermissionCollection buildPermissionCollection(
        Permission... permissions) {
        return new PermissionCollection(new HashSet<Permission>(Arrays.asList(permissions)));
    }

}
