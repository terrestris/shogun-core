package de.terrestris.shogun2.security.access.entity;

import de.terrestris.shogun2.model.Application;

/**
 * The {@link Application} class is used as an example class for secured objects.
 *
 * @author Nils Bühner
 *
 */
public class SecuredObjectPermissionEvaluatorTest extends
		AbstractPersistentObjectPermissionEvaluatorTest<Application> {

	public SecuredObjectPermissionEvaluatorTest() {
		super(Application.class, new PersistentObjectPermissionEvaluator<>(Application.class), new Application());
	}

}
