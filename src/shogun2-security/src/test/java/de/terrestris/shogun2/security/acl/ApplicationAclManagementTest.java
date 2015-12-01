/**
 *
 */
package de.terrestris.shogun2.security.acl;

import org.springframework.beans.factory.annotation.Autowired;

import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.service.AbstractCrudService;
import de.terrestris.shogun2.service.ApplicationService;

/**
 * @author Nils Bühner
 *
 */
public class ApplicationAclManagementTest extends
		AbstractAclManagementTest<Application> {

	@Autowired
	private ApplicationService applicationService;

	@Override
	protected Application getImplToUse() {
		return new Application("Testapp", "Description");
	}

	@Override
	protected AbstractCrudService<Application> getCrudService() {
		return applicationService;
	}

}
