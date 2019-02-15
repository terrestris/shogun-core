/**
 *
 */
package de.terrestris.shoguncore.security.access.entity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.terrestris.shoguncore.helper.IdHelper;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.security.Permission;

/**
 * @author Nils Bühner
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
        final User user = new User("First name", "Last Name", "accountName");
        IdHelper.setIdOnPersistentObject(user, 42);

        // we do not add any permissions to the user, but expect that he is allowed to READ himself
        // call method to test
        boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user, user, readPermission);

        assertThat(permissionResult, equalTo(true));

    }

    @Test
    public void hasPermission_shouldNeverGrantAdminDeleteOrWriteOnOwnUserObject() throws NoSuchFieldException, IllegalAccessException {

        // prepare a user that
        final User user = new User("First name", "Last Name", "accountName");
        IdHelper.setIdOnPersistentObject(user, 42);

        Set<Permission> permissions = new HashSet<Permission>(Arrays.asList(Permission.values()));
        permissions.remove(Permission.READ); // everything but READ

        for (Permission permission : permissions) {
            // call method to test
            boolean permissionResult = persistentObjectPermissionEvaluator.hasPermission(user, user, permission);

            assertThat(permissionResult, equalTo(false));
        }

    }

}
