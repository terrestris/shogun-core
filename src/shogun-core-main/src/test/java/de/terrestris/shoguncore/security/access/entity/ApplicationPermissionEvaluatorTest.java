/**
 *
 */
package de.terrestris.shoguncore.security.access.entity;

import de.terrestris.shoguncore.model.Application;

/**
 * @author Nils Bühner
 */
public class ApplicationPermissionEvaluatorTest extends
    AbstractPersistentObjectPermissionEvaluatorTest<Application> {

    public ApplicationPermissionEvaluatorTest() {
        super(Application.class, new PersistentObjectPermissionEvaluator<>(Application.class), new Application());
    }

}
