package de.terrestris.shogun2.service;

import de.terrestris.shogun2.model.User;

public class UserServiceTest extends AbstractExtDirectCrudServiceTest<User> {

	/**
	 * 
	 * @throws Exception
	 */
	public void setUpImplToTest() throws Exception {
		implToTest = new User();
	}

	/**
	 * 
	 */
	@Override
	protected AbstractExtDirectCrudService<User> getCrudService() {
		return new UserService();
	}

}
