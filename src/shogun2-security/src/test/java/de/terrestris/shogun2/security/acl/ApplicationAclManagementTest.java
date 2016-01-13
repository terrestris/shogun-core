/**
 *
 */
package de.terrestris.shogun2.security.acl;

import org.springframework.beans.factory.annotation.Autowired;

import de.terrestris.shogun2.dao.ApplicationDao;
import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.service.AbstractCrudService;
import de.terrestris.shogun2.service.ApplicationService;

/**
 * @author Nils Bühner
 *
 */
public class ApplicationAclManagementTest extends
		AbstractAclManagementTest<Application, ApplicationDao<Application>> {

	@Autowired
	private ApplicationService<Application, ApplicationDao<Application>> applicationService;

	@Override
	protected Application getImplToUse() {
		return new Application("Testapp", "Description");
	}

	@Override
	protected AbstractCrudService<Application, ApplicationDao<Application>> getCrudService() {
		return applicationService;
	}

}
