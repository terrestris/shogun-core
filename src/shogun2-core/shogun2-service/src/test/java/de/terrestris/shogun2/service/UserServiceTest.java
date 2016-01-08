package de.terrestris.shogun2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.terrestris.shogun2.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-encoder-bean.xml" })
public class UserServiceTest extends AbstractExtDirectCrudServiceTest<User> {

	@Mock
	private RegistrationTokenService registrationTokenService;

	/**
	 * The autowired PasswordEncoder
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Before
	public void setUp() {
		super.setUp();

		// set the pw encoder
		((UserService) crudService).setPasswordEncoder(passwordEncoder);
	}

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

	@Test(expected=HibernateException.class)
	public void findByAccountName_shouldThrowHibernateException() {
		String accountName = "erroraccount";

		// mock the dao
		doThrow(new HibernateException("errormsg"))
			.when(dao).findByUniqueCriteria(any(SimpleExpression.class));

		((UserService) crudService).findByAccountName(accountName);

		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verifyNoMoreInteractions(dao);
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

	@Test(expected=HibernateException.class)
	public void findByEmail_shouldThrowHibernateException() {
		String email = "errormail@example.com";

		// mock the dao
		doThrow(new HibernateException("errormsg"))
			.when(dao).findByUniqueCriteria(any(SimpleExpression.class));

		((UserService) crudService).findByEmail(email);

		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verifyNoMoreInteractions(dao);
	}

	@Test
	public void registerUser_shouldRegisterNonExistingUserAsExpected() throws Exception {
		String email = "test@example.com";
		String rawPassword = "p@sSw0rd";
		boolean isActive = false;

		// mock the dao
		// there is no existing user -> return null (in the findByEmail method)
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(null);

		// the saveOrUpdate will be called in the persistNewUser method
		doNothing().when(dao).saveOrUpdate(any(User.class));

		// mock the registrationTokenService (which sends the mail)
		doNothing().when(registrationTokenService)
				.sendRegistrationActivationMail(
						any(HttpServletRequest.class),
						any(User.class));

		HttpServletRequest requestMock = mock(HttpServletRequest.class);

		// finally call the method that is tested here
		User registeredUser = ((UserService) crudService).registerUser(email, rawPassword, isActive, requestMock);

		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verify(dao, times(1)).saveOrUpdate(any(User.class));
		verifyNoMoreInteractions(dao);

		verify(registrationTokenService, times(1))
				.sendRegistrationActivationMail(
						any(HttpServletRequest.class),
						any(User.class));
		verifyNoMoreInteractions(registrationTokenService);

		assertTrue(passwordEncoder.matches(rawPassword, registeredUser.getPassword()));
		assertEquals(email, registeredUser.getAccountName());
		assertEquals(email, registeredUser.getEmail());
		assertEquals(isActive, registeredUser.isActive());
	}

	@Test
	public void registerUser_shouldThrowExceptionIfUserAlreadyExists() {
		String email = "test@example.com";
		String rawPassword = "p@sSw0rd";
		boolean isActive = false;

		User existingUser = new User();
		existingUser.setEmail(email);

		// there is an existing user -> return null (in the findByEmail method)
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(existingUser);

		HttpServletRequest requestMock = mock(HttpServletRequest.class);
		// finally call the method that is tested here
		try {
			((UserService) crudService).registerUser(email, rawPassword, isActive, requestMock);
			fail("Should have thrown Exception, but did not!");
		} catch (Exception e) {
			final String msg = e.getMessage();
			assertEquals("User with eMail '" + email + "' already exists.", msg);
		}

	}

}
