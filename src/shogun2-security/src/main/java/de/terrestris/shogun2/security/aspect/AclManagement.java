package de.terrestris.shogun2.security.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * Aspect-class to manage ACL entries when SHOGun2-objects are saved, updated or
 * deleted by the AbstractCrudService.
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
	 * Creates/updates the ACL data when saveOrUpdate (of the
	 * AbstractCrudService) is called.
	 * 
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("execution(* de.terrestris.shogun2.service.AbstractCrudService.saveOrUpdate(..))")
	public void manageAclOnSaveOrUpdate(ProceedingJoinPoint joinPoint)
			throws Throwable {
		LOG.debug("manageAclWhenSaveOrUpdate is running");

		// get argument (= object) of saveOrUpdate-call
		PersistentObject objectToPersist = (PersistentObject) joinPoint
				.getArgs()[0];

		// decide whether the object will be created or updated...
		boolean objectIsUpdated = objectToPersist.getId() != null;

		// actually call the intercepted method
		PersistentObject persistedObject = (PersistentObject) joinPoint
				.proceed();

		LOG.debug("saveOrUpdate returned: " + persistedObject);

		// handle ACL entries if object has been persisted
		if (objectIsUpdated) {
			// TODO delegate the object to some kind of ACL processor that will
			// update existing ACL entries correctly (according to the type of
			// the object).
		} else {
			// TODO delegate the object to some kind of ACL processor that will
			// create correct ACL entries correctly (according to the type of
			// the object).
		}
	}

	/**
	 * Removes the ACL data after AbstractCrudService.delete has been called.
	 * 
	 * @param joinPoint
	 */
	@After("execution(* de.terrestris.shogun2.service.AbstractCrudService.delete(..))")
	public void manageAclAfterDelete(JoinPoint joinPoint) {
		LOG.debug("manageAclAfterDelete is running");

		// get argument (= object) of delete-call
		PersistentObject deletedObject = (PersistentObject) joinPoint.getArgs()[0];

		// TODO delegate the object to some kind of ACL processor that will
		// delete existing ACL entries correctly (according to the type of the
		// object).

		LOG.debug("Deleted " + deletedObject);
	}

}
