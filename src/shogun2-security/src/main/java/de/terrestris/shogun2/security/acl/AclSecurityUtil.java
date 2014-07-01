package de.terrestris.shogun2.security.acl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;

/**
 * @author Nils Bühner
 * 
 */
@Component
@Transactional("aclTransactionManager")
public class AclSecurityUtil {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger.getLogger(AclSecurityUtil.class);

	@Autowired
	private MutableAclService aclService;

	/**
	 * Adds a permission entry to the ACL database.
	 * 
	 * @param objectToSecure
	 *            The object to secure.
	 * @param user
	 *            The user to give permission for the object.
	 * @param permission
	 *            The permission to grant.
	 */
	public void addPermission(PersistentObject objectToSecure, User user,
			Permission permission) {
		MutableAcl acl;
		ObjectIdentity oid = new ObjectIdentityImpl(objectToSecure);

		// Get existing ACL or create a new one.
		try {
			acl = (MutableAcl) aclService.readAclById(oid);
		} catch (NotFoundException nfe) {
			acl = aclService.createAcl(oid);
		}

		// Insert access control entry
		Sid recipient = new PrincipalSid(user.getAccountName());
		acl.insertAce(acl.getEntries().size(), permission, recipient, true);
		LOG.debug("Added ACE: '" + permission + "' for '" + recipient
				+ "' on '" + oid);

		acl = aclService.updateAcl(acl);
	}

	/**
	 * Deletes a permission from the ACL database.
	 * 
	 * @param securedObject
	 *            The secured object.
	 * @param clazz
	 *            The concrete class of the securedObject
	 * @param user
	 *            The user, that has a permission for the securedObject.
	 * @param permission
	 *            The permission, the user has for the securedObject.
	 */
	public void deletePermission(PersistentObject securedObject, User user,
			Permission permission) {

		// Prepare basic information
		Sid recipient = new PrincipalSid(user.getAccountName());
		String type = securedObject.getClass().getCanonicalName();
		Integer id = securedObject.getId();

		// Build ACL objects
		ObjectIdentity oid = new ObjectIdentityImpl(type, id);
		MutableAcl acl = (MutableAcl) aclService.readAclById(oid);

		// Remove all permissions associated with this particular recipient
		// (string equality used to keep things simple)
		List<AccessControlEntry> entries = acl.getEntries();
		int i = 0;
		for (AccessControlEntry entry : entries) {
			if (entry.getSid().equals(recipient)
					&& entry.getPermission().equals(permission)) {
				acl.deleteAce(i);
				LOG.debug("Deleting ACE: '" + permission + "' for '"
						+ recipient + "' on '" + oid);
			}
			i++;
		}

		acl = aclService.updateAcl(acl);
	}
}
