package de.terrestris.shogun2.security.acl;

import javax.sql.DataSource;

import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import de.terrestris.shogun2.model.User;

/**
 * @author Nils Bühner
 * 
 */
public class Shogun2JdbcMutableAclService extends JdbcMutableAclService {

	/**
	 * Just call the parent constructor.
	 * 
	 * @param dataSource
	 * @param lookupStrategy
	 * @param aclCache
	 */
	public Shogun2JdbcMutableAclService(DataSource dataSource,
			LookupStrategy lookupStrategy, AclCache aclCache) {
		super(dataSource, lookupStrategy, aclCache);
	}

	/**
	 * Almost the same implementation as in parent class, except that we take
	 * care in case that the principal object of the authentication object is a
	 * SHOGun2 user object.
	 * 
	 * Behaves like the parent class if the principal is not of type
	 * {@link User}.
	 */
	@Override
	public MutableAcl createAcl(ObjectIdentity objectIdentity)
			throws AlreadyExistsException {
		Assert.notNull(objectIdentity, "Object Identity required");

		// Check this object identity hasn't already been persisted
		if (retrieveObjectIdentityPrimaryKey(objectIdentity) != null) {
			throw new AlreadyExistsException("Object identity '"
					+ objectIdentity + "' already exists");
		}

		// Need to retrieve the current principal, in order to know who "owns"
		// this ACL (can be changed later on)
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		Object principal = auth.getPrincipal();

		PrincipalSid sid;
		// check if principal is a SHOGun2 user
		if (principal instanceof User) {
			// User the accountName to create the PrincipalSid
			sid = new PrincipalSid(((User) principal).getAccountName());
		} else {
			// default behaviour
			sid = new PrincipalSid(auth);
		}

		// Create the acl_object_identity row
		createObjectIdentity(objectIdentity, sid);

		// Retrieve the ACL via superclass (ensures cache registration, proper
		// retrieval etc)
		Acl acl = readAclById(objectIdentity);
		Assert.isInstanceOf(MutableAcl.class, acl,
				"MutableAcl should be been returned");

		return (MutableAcl) acl;
	}

}
