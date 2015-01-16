package de.terrestris.shogun2.service;

import de.terrestris.shogun2.model.Application;

public class ApplicationServiceTest extends
		AbstractExtDirectCrudServiceTest<Application> {

	/**
	 * 
	 * @throws Exception
	 */
	public void setUpImplToTest() throws Exception {
		implToTest = new Application();
	}

	/**
	 * 
	 */
	@Override
	protected AbstractExtDirectCrudService<Application> getCrudService() {
		return new ApplicationService();
	}

}
