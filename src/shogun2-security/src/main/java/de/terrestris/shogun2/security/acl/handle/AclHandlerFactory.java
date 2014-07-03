package de.terrestris.shogun2.security.acl.handle;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * Interface, which provides a method to create an {@link AbstractAclHandler}. A
 * concrete implementation of this interface, that should be used in an
 * application, has to be defined in the spring context.
 * 
 * @author Nils Bühner
 * 
 */
public interface AclHandlerFactory {

	public AbstractAclHandler<? extends PersistentObject> getAclHandler(
			PersistentObject persistedObject) throws Exception;

}
