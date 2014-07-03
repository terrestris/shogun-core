package de.terrestris.shogun2.security.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.security.acl.handle.AbstractAclHandler;
import de.terrestris.shogun2.security.acl.handle.AclHandlerFactory;

/**
 * Aspect-class to intercept methods of the AbstractCrudService for the
 * management of ACL entries.
 * 
 * @author Nils Bühner
 * 
 */
@Aspect
public class AclManagement {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger.getLogger(AclManagement.class);

	/**
	 * A concrete implementation of an {@link AclHandlerFactory} has to be
	 * defined in the spring context.
	 */
	private AclHandlerFactory aclHandlerFactory;

	/**
	 * Intercepts the saveOrUpdate method of the AbstractCrudService and
	 * delegates the persisted object to an appropriate
	 * {@link AbstractAclHandler}, which will create/update the necessary ACL
	 * entries.
	 * 
	 * @param joinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(* de.terrestris.shogun2.service.AbstractCrudService.saveOrUpdate(..))")
	public PersistentObject handleAclOnSaveOrUpdate(
			ProceedingJoinPoint joinPoint) {

		// get argument (= object) of saveOrUpdate-call
		PersistentObject objectToPersist = (PersistentObject) joinPoint
				.getArgs()[0];

		// decide whether the object will be created or updated...
		boolean objectIsUpdated = objectToPersist.getId() != null;

		String tmpLog = objectIsUpdated ? "update" : "save";
		LOG.debug("Intercepting " + tmpLog + " of " + objectToPersist);

		PersistentObject persistedObject = null;
		try {
			// actually invoke the intercepted method
			persistedObject = (PersistentObject) joinPoint.proceed();

			String continueLog = " Continue with ACL handling now.";
			if (objectIsUpdated) {
				tmpLog = "Updated the object.";
			} else {
				tmpLog = "Saved object with id " + persistedObject.getId()
						+ ".";
			}
			LOG.debug(tmpLog + continueLog);

			try {
				// get a correct ACL handler for the object
				AbstractAclHandler<? extends PersistentObject> aclHandler = aclHandlerFactory
						.getAclHandler(persistedObject);

				// handle ACL entries if object has been persisted
				if (objectIsUpdated) {
					aclHandler.updateAclEntries();
				} else {
					aclHandler.createAclEntries();
				}
			} catch (Exception e) {
				LOG.error("Could not handle the creation or deletion of ACLs. Reason: "
						+ e.getMessage());
			}
		} catch (Throwable e) {
			LOG.error("Could not invoke intercepted saveOrUpdate method: "
					+ e.getMessage());
		}

		return persistedObject;
	}

	/**
	 * Intercepts the delete method of the AbstractCrudService and delegates the
	 * object to an appropriate {@link AbstractAclHandler}, which will delete
	 * the necessary ACL entries.
	 * 
	 * @param joinPoint
	 */
	@After("execution(* de.terrestris.shogun2.service.AbstractCrudService.delete(..))")
	public void handleAclAfterDelete(JoinPoint joinPoint) {

		// get argument (= object) of delete-call
		PersistentObject deletedObject = (PersistentObject) joinPoint.getArgs()[0];

		LOG.debug("Handling ACL entries after the deletion of " + deletedObject);

		AbstractAclHandler<? extends PersistentObject> aclHandler;
		try {
			aclHandler = aclHandlerFactory.getAclHandler(deletedObject);
			// actually delete the ACLs
			aclHandler.deleteAclEntries();
		} catch (Exception e) {
			LOG.error("Could not handle the deletion of ACLs. Reason: "
					+ e.getMessage());
		}

	}

	/**
	 * @return the aclHandlerFactory
	 */
	public AclHandlerFactory getAclHandlerFactory() {
		return aclHandlerFactory;
	}

	/**
	 * @param aclHandlerFactory
	 *            the aclHandlerFactory to set
	 */
	public void setAclHandlerFactory(AclHandlerFactory aclHandlerFactory) {
		this.aclHandlerFactory = aclHandlerFactory;
	}

}
