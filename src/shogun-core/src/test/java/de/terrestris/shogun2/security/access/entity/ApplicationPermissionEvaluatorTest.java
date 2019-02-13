/**
 *
 */
package de.terrestris.shogun2.security.access.entity;

import de.terrestris.shogun2.model.Application;

/**
 * @author Nils Bühner
 */
public class ApplicationPermissionEvaluatorTest extends
    AbstractPersistentObjectPermissionEvaluatorTest<Application> {

    public ApplicationPermissionEvaluatorTest() {
        super(Application.class, new PersistentObjectPermissionEvaluator<>(Application.class), new Application());
    }

}
