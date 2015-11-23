package de.terrestris.shogun2.security.acl;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.terrestris.shogun2.security.aspect.AclManagement;

/**
 * Test class to test that the aspect annotated class {@link AclManagement}
 * intercepts the AbstractCrudService as expected.
 *
 * @author Nils Bühner
 *
 */
// The context defined here will extend (!) the context of the parent class!
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-context-acl-management.xml" })
public class AclManagementTest extends AclTestBase {

	/**
	 *
	 */
	@Test
	public void handleAclOnSaveOrUpdate_interceptsAsExpected() {
	}

	/**
	 *
	 */
	@Test
	public void handleAclAfterDelete_interceptsAsExpected() {
	}
}
