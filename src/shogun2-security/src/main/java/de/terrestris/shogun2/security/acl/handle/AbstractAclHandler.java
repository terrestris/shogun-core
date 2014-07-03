package de.terrestris.shogun2.security.acl.handle;

import org.apache.log4j.Logger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.context.SecurityContextHolder;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.security.acl.AclUtil;

/**
 * Abstract class that provides methods to handle (save/update/delete) ACL
 * entries for a given class/object.
 * 
 * @author Nils Bühner
 * 
 */
public abstract class AbstractAclHandler<E extends PersistentObject> {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger
			.getLogger(AbstractAclHandler.class);

	/**
	 * The object that has been saved, updated or deleted and whose ACL entries
	 * have to be updated.
	 */
	protected E object;

	/**
	 * The logged in user, who saved, updated or deleted the {@link #object}.
	 */
	protected User loggedInUser;

	/**
	 * The ACL util to add or delete ACL permissions.
	 */
	protected AclUtil aclUtil;

	/**
	 * Constructor.
	 * 
	 * @param object
	 * @param aclUtil
	 */
	public AbstractAclHandler(E object, AclUtil aclUtil) {
		this.object = object;
		this.aclUtil = aclUtil;

		// set the currently logged in user
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		if (principal instanceof User) {
			this.loggedInUser = (User) principal;
		}
	};

	/**
	 * A user that has created a object, will always get READ, WRITE and DELETE
	 * permissions on that object.
	 * 
	 * This can be overwritten in a subclass.
	 */
	public void createAclEntries() {
		aclUtil.addPermission(object, loggedInUser, BasePermission.READ);
		aclUtil.addPermission(object, loggedInUser, BasePermission.WRITE);
		aclUtil.addPermission(object, loggedInUser, BasePermission.DELETE);

		LOG.info("Created ACL entries for " + object);
	};

	/**
	 * When a user deleted an object, we will always delete the READ, WRITE and
	 * DELETE permissions on that object.
	 * 
	 * If one of those permissions should not exist, nothing will happen as
	 * {@link AclUtil#deletePermission(PersistentObject, User, org.springframework.security.acls.model.Permission)}
	 * will first check if such a permission exists.
	 * 
	 * This can be overwritten in a subclass.
	 */
	public void deleteAclEntries() {
		aclUtil.deletePermission(object, loggedInUser, BasePermission.READ);
		aclUtil.deletePermission(object, loggedInUser, BasePermission.WRITE);
		aclUtil.deletePermission(object, loggedInUser, BasePermission.DELETE);

		LOG.info("Deleted ACL entries for " + object);
	};

	/**
	 * Abstract method, that has to be implemented in subclasses.
	 */
	public abstract void updateAclEntries();

}
