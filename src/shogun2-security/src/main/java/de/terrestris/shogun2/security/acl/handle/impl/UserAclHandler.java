package de.terrestris.shogun2.security.acl.handle.impl;

import org.apache.log4j.Logger;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.security.acl.AclUtil;
import de.terrestris.shogun2.security.acl.handle.AbstractAclHandler;

/**
 * @author Nils Bühner
 * 
 */
public class UserAclHandler extends AbstractAclHandler<User> {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger.getLogger(UserAclHandler.class);

	/**
	 * Constructor
	 * 
	 * @param object
	 * @param aclUtil
	 */
	public UserAclHandler(User object, AclUtil aclUtil) {
		super(object, aclUtil);
	}

	/**
	 * 
	 */
	@Override
	public void updateAclEntries() {
		LOG.info("Update ACLs for " + object);
		// TODO implement, if necessary
	}

}
