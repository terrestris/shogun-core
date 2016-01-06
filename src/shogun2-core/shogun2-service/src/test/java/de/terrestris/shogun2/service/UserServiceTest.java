package de.terrestris.shogun2.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.hibernate.criterion.SimpleExpression;
import org.junit.Test;

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

	@Test
	public void findByAccountName_shouldFindAsExpected() {
		String accountName = "testaccount";

		User expectedUser = new User("Test", "User", accountName);

		// mock the dao
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(expectedUser);

		User actualUser = ((UserService) crudService).findByAccountName(accountName);

		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verifyNoMoreInteractions(dao);

		assertEquals(expectedUser, actualUser);
	}

	@Test
	public void findByAccountName_shouldFindNothing() {
		String accountName = "nonexistingaccount";

		User expectedUser = null;

		// mock the dao
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(expectedUser);

		User actualUser = ((UserService) crudService).findByAccountName(accountName);

		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verifyNoMoreInteractions(dao);

		assertEquals(expectedUser, actualUser);
	}

	@Test
	public void findByEmail_shouldFindAsExpected() {
		String eMail = "mail@example.com";

		User expectedUser = new User();
		expectedUser.setEmail(eMail);

		// mock the dao
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(expectedUser);

		User actualUser = ((UserService) crudService).findByEmail(eMail);

		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verifyNoMoreInteractions(dao);

		assertEquals(expectedUser, actualUser);
	}

	@Test
	public void findByEmail_shouldFindNothing() {
		String eMail = "nonexisting@example.com";

		User expectedUser = null;

		// mock the dao
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(expectedUser);

		User actualUser = ((UserService) crudService).findByEmail(eMail);

		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verifyNoMoreInteractions(dao);

		assertEquals(expectedUser, actualUser);
	}

}
