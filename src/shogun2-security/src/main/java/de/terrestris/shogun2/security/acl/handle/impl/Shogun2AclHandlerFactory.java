package de.terrestris.shogun2.security.acl.handle.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.Person;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.security.acl.AclUtil;
import de.terrestris.shogun2.security.acl.handle.AbstractAclHandler;
import de.terrestris.shogun2.security.acl.handle.AclHandlerFactory;

/**
 * SHOGun2 default implementation of the {@link AclHandlerFactory} interface.
 * 
 * @author Nils Bühner
 * 
 */
public class Shogun2AclHandlerFactory implements AclHandlerFactory {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger
			.getLogger(Shogun2AclHandlerFactory.class);

	/**
	 * The util used to add/delete ACL permissions.
	 */
	@Autowired
	protected AclUtil aclUtil;

	/**
	 * Checks the type/class of the passed object and returns a concrete
	 * implementation of link {@link AbstractAclHandler} if available.
	 * 
	 * This implementation has to be extended, if a new SHOGun2 model class has
	 * been added.
	 * 
	 * @throws Exception
	 */
	@Override
	public AbstractAclHandler<? extends PersistentObject> getAclHandler(
			PersistentObject object) throws Exception {

		LOG.debug("Getting ACL Handler for type " + object.getClass());

		// Be careful here about the order in which the SHOGun2-model classes
		// are checked. As long as "instanceof" is used, you should start at the
		// bottom of a hierarchy and step up to ensure that you always get
		// the most specific AclHandler, e.g. "User" class should be checked
		// before "Person".
		if (object instanceof Application) {
			return new ApplicationAclHandler((Application) object, aclUtil);
		}

		else if (object instanceof User) {
			return new UserAclHandler((User) object, aclUtil);
		}

		else if (object instanceof Person) {
			return new PersonAclHandler((Person) object, aclUtil);
		}

		else {
			throw new Exception(
					"Could not build an appropriate ACL handler for the type "
							+ object.getClass());
		}

	}

}
