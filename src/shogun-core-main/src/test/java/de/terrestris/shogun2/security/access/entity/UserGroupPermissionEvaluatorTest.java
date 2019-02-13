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

import de.terrestris.shogun2.helper.IdHelper;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.model.security.Permission;

/**
 * @author Nils Bühner
 */
public class UserGroupPermissionEvaluatorTest extends
    AbstractPersistentObjectPermissionEvaluatorTest<UserGroup> {

    public UserGroupPermissionEvaluatorTest() {
        super(UserGroup.class, new UserGroupPermissionEvaluator<>(), new UserGroup());
    }

    @Test
    public void hasPermission_shouldAlwaysGrantReadOnGroupsWhereUserIsMember() throws NoSuchFieldException, IllegalAccessException {
        Permission readPermission = Permission.READ;

        // prepare a user that is member of the group
        final User user = new User("First name", "Last Name", "accountName");
        IdHelper.setIdOnPersistentObject(user, 42);

        // prepare a secured group
        UserGroup userGroup = new UserGroup();
        IdHelper.setIdOnPersistentObject(user, 17);

        // add the user to the group
        userGroup.getMembers().add(user);

        // we do not add any permissions to the user, but expect that he is allowed to READ himself
        // call method to test
        boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user, userGroup, readPermission);

        assertThat(permissionResult, equalTo(true));

    }

    @Test
    public void hasPermission_shouldNeverGrantAdminDeleteOrWriteOnOwnUserObject() throws NoSuchFieldException, IllegalAccessException {

        // prepare a user that
        final User user = new User("First name", "Last Name", "accountName");
        IdHelper.setIdOnPersistentObject(user, 42);

        // prepare a secured group
        UserGroup userGroup = new UserGroup();
        IdHelper.setIdOnPersistentObject(user, 17);

        // add the user to the group
        userGroup.getMembers().add(user);

        Set<Permission> permissions = new HashSet<Permission>(Arrays.asList(Permission.values()));
        permissions.remove(Permission.READ); // everything but READ

        for (Permission permission : permissions) {
            // call method to test
            boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user, userGroup, permission);

            assertThat(permissionResult, equalTo(false));
        }

    }

}
