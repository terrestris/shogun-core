package de.terrestris.shogun2.security.acl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;

/**
 * @author Nils Bühner
 *
 */
public class AclUtilTest extends AbstractAclTestBase {

	/**
	 * The AclUtil that will be tested here.
	 */
	@Autowired
	AclUtil aclUtil;

	/**
	 * Tests whether the AclUtil will correctly add a permission.
	 *
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@Test
	public void addPermission_shouldAddPermission()
			throws NoSuchFieldException, IllegalAccessException {

		Permission permissionToAdd = BasePermission.READ;

		aclUtil.addPermission(objectToSecure, user, permissionToAdd);

		checkUserPermissionOnObject(objectToSecure, user, permissionToAdd, 1,
				true);
	}

	/**
	 * Tests whether the AclUtil will correctly update a given permission.
	 */
	@Test
	public void updatePermission_shouldUpdatePermission() {
		Permission oldPermission = BasePermission.READ;
		Permission newPermission = BasePermission.DELETE;

		// we need a permission to update...
		createAclAndAddPermission(oldPermission);

		int expectedAclEntriesSize = 1;
		boolean shouldHavePermission = true;
		// be sure that we really added the entry
		checkUserPermissionOnObject(objectToSecure, user, oldPermission,
				expectedAclEntriesSize, shouldHavePermission);

		// invoke the method we want to test
		aclUtil.updatePermission(objectToSecure, user, oldPermission,
				newPermission);

		// actually test
		// user should NOT have old permission
		shouldHavePermission = false;
		checkUserPermissionOnObject(objectToSecure, user, oldPermission,
				expectedAclEntriesSize, shouldHavePermission);

		// but user should now have the new permission instead
		shouldHavePermission = true;
		checkUserPermissionOnObject(objectToSecure, user, newPermission,
				expectedAclEntriesSize, shouldHavePermission);
	}

	/**
	 * Tests whether the AclUtil will correctly delete a given permission.
	 */
	@Test
	public void deletePermission_shouldDeletePermission() {
		Permission permissionToDelete = BasePermission.CREATE;

		// we need a permission to delete...
		createAclAndAddPermission(permissionToDelete);

		int expectedAclEntriesSize = 1;
		boolean shouldHavePermission = true;

		// be sure that we really added the entry
		checkUserPermissionOnObject(objectToSecure, user, permissionToDelete,
				expectedAclEntriesSize, shouldHavePermission);

		// invoke the method we want to test
		aclUtil.deletePermission(objectToSecure, user, permissionToDelete);

		// actually test
		// user should NOT have the permission anymore
		expectedAclEntriesSize = 0;
		shouldHavePermission = false;
		checkUserPermissionOnObject(objectToSecure, user, permissionToDelete,
				expectedAclEntriesSize, shouldHavePermission);
	}

	/**
	 * This will "manually" create an (not yet existing) ACL with the given
	 * permission
	 *
	 * @param permission
	 */
	private void createAclAndAddPermission(Permission permission) {
		// "manually" add a permission
		ObjectIdentity oid = new ObjectIdentityImpl(objectToSecure);
		MutableAcl acl = aclService.createAcl(oid);
		Sid recipient = new PrincipalSid(user.getAccountName());
		acl.insertAce(acl.getEntries().size(), permission, recipient, true);
		aclService.updateAcl(acl);
	}

	/**
	 * Checks user permissions on the passed object.
	 *
	 * @param securedObject
	 * @param userWithPermission
	 * @param permission
	 * @param expectedEntriesSize
	 *            the number of expected ACL entries
	 * @param shouldHavePermission
	 *            whether or not the user should have the permission
	 */
	private void checkUserPermissionOnObject(PersistentObject securedObject,
			User userWithPermission, Permission permission,
			int expectedEntriesSize, boolean shouldHavePermission) {

		assertNotNull(securedObject);
		assertNotNull(userWithPermission);
		assertNotNull(permission);

		// build acl object identity and request acl for the object.
		ObjectIdentity oi = new ObjectIdentityImpl(securedObject);
		assertNotNull(oi);

		Acl acl = aclService.readAclById(oi);
		assertNotNull(acl);

		// build sid for user
		Sid sid = new PrincipalSid(user.getAccountName());

		List<AccessControlEntry> aclEntries = acl.getEntries();
		assertNotNull(aclEntries);

		assertEquals(expectedEntriesSize, aclEntries.size());

		// finally check if we find the permission
		boolean userHasPermission = false;
		for (AccessControlEntry aclEntry : aclEntries) {
			if (aclEntry.getSid().equals(sid)
					&& aclEntry.getPermission().equals(permission)) {
				userHasPermission = true;
			}
		}

		if (shouldHavePermission) {
			assertTrue("User does NOT have permission.", userHasPermission);
		} else {
			assertTrue("User DOES have permission.", !userHasPermission);
		}
	}
}
