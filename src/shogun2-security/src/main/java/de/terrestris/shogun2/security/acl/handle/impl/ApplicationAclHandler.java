package de.terrestris.shogun2.security.acl.handle.impl;

import org.apache.log4j.Logger;

import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.security.acl.AclUtil;
import de.terrestris.shogun2.security.acl.handle.AbstractAclHandler;

/**
 * @author Nils Bühner
 * 
 */
public class ApplicationAclHandler extends AbstractAclHandler<Application> {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger
			.getLogger(ApplicationAclHandler.class);

	/**
	 * Constructor
	 * 
	 * @param object
	 * @param aclUtil
	 */
	public ApplicationAclHandler(Application object, AclUtil aclUtil) {
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
