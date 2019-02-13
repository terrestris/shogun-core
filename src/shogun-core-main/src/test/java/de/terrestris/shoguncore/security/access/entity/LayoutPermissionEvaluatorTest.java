package de.terrestris.shoguncore.security.access.entity;

import de.terrestris.shoguncore.model.layout.Layout;

/**
 * The {@link Layout} class is used as an example class for unsecured objects.
 *
 * @author Nils Bühner
 */
public class LayoutPermissionEvaluatorTest extends
    AbstractPersistentObjectPermissionEvaluatorTest<Layout> {

    public LayoutPermissionEvaluatorTest() {
        super(Layout.class, new PersistentObjectPermissionEvaluator<>(Layout.class), new Layout());
    }

}
