package de.terrestris.shogun2.security.access.entity;

import de.terrestris.shogun2.model.layout.Layout;

/**
 * The {@link Layout} class is used as an example class for unsecured objects.
 *
 * @author Nils Bühner
 *
 */
public class LayoutPermissionEvaluatorTest extends
	AbstractUnsecuredObjectPermissionEvaluatorTest<Layout> {

	public LayoutPermissionEvaluatorTest() {
		super(Layout.class, new PersistentObjectPermissionEvaluator<>(Layout.class), new Layout());
	}

}
