package de.terrestris.shogun2.security.access.entity;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * @author Nils Bühner
 *
 */
public abstract class AbstractPersistentObjectPermissionEvaluatorTest<E extends PersistentObject> {

	// the permission evaluator to test
	protected PersistentObjectPermissionEvaluator<E> persistentObjectPermissionEvaluator;

	protected final Class<E> entityClass;

	protected E entityToCheck;

	/**
	 * Constructor that has to be implemented by subclasses
	 *
	 * @param entityClass
	 */
	protected AbstractPersistentObjectPermissionEvaluatorTest(
			Class<E> entityClass,
			PersistentObjectPermissionEvaluator<E> persistentObjectPermissionEvaluator,
			E entityToCheck) {
		this.entityClass = entityClass;
		this.persistentObjectPermissionEvaluator = persistentObjectPermissionEvaluator;
		this.entityToCheck = entityToCheck;
	}

}
