package de.terrestris.shogun2.security.acl.handle.impl;

import org.apache.log4j.Logger;

import de.terrestris.shogun2.model.Person;
import de.terrestris.shogun2.security.acl.AclUtil;
import de.terrestris.shogun2.security.acl.handle.AbstractAclHandler;

/**
 * @author Nils Bühner
 * 
 */
public class PersonAclHandler extends AbstractAclHandler<Person> {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger.getLogger(PersonAclHandler.class);

	/**
	 * Constructor
	 * 
	 * @param object
	 * @param aclUtil
	 */
	public PersonAclHandler(Person object, AclUtil aclUtil) {
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
