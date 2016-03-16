package de.terrestris.shogun2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import de.terrestris.shogun2.dao.RegistrationTokenDao;
import de.terrestris.shogun2.dao.RoleDao;
import de.terrestris.shogun2.dao.UserDao;
import de.terrestris.shogun2.helper.IdHelper;
import de.terrestris.shogun2.model.Role;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.RegistrationToken;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-encoder-bean.xml" })
public class UserServiceTest extends AbstractExtDirectCrudServiceTest<User, UserDao<User>, UserService<User, UserDao<User>>> {

	@Mock
	private RegistrationTokenService<RegistrationToken, RegistrationTokenDao<RegistrationToken>> registrationTokenService;

	@Mock
	private RoleService<Role, RoleDao<Role>> roleService;

	@Mock
	private Role defaultUserRole;

	/**
	 * The autowired PasswordEncoder
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Before
	public void setUp() {
		super.setUp();

		// set the pw encoder
		crudService.setPasswordEncoder(passwordEncoder);
	}

	/**
	 *
	 * @throws Exception
	 */
	public void setUpImplToTest() throws Exception {
		implToTest = new User();
	}

	@Override
	protected UserService<User, UserDao<User>> getCrudService() {
		return new UserService<User, UserDao<User>>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<UserDao<User>> getDaoClass() {
		return (Class<UserDao<User>>) new UserDao<User>().getClass();
	}

	@Test
	public void findByAccountName_shouldFindAsExpected() {
		String accountName = "testaccount";

		User expectedUser = new User("Test", "User", accountName);

		// mock the dao
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(expectedUser);

		User actualUser = crudService.findByAccountName(accountName);

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

		User actualUser = crudService.findByAccountName(accountName);

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

		crudService.findByAccountName(accountName);

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

		User actualUser = crudService.findByEmail(eMail);

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

		User actualUser = crudService.findByEmail(eMail);

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

		crudService.findByEmail(email);

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

		// create user instance
		User user = new User();
		user.setEmail(email);
		user.setAccountName(email);
		user.setPassword(rawPassword);
		user.setActive(isActive);

		// finally call the method that is tested here
		User registeredUser = crudService.registerUser(user, requestMock);

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
			// create user instance
			User user = new User();
			user.setEmail(email);
			user.setAccountName(email);
			user.setPassword(rawPassword);
			user.setActive(isActive);

			crudService.registerUser(user, requestMock);
			fail("Should have thrown Exception, but did not!");
		} catch (Exception e) {
			final String msg = e.getMessage();
			assertEquals("User with eMail '" + email + "' already exists.", msg);
		}

	}

	@Test
	public void activateUser_shouldActivateUserAsExpected() throws Exception {

		// an inactive user that is assigend to a token
		User user = new User();
		user.setActive(false);

		// create a token that is associated with the user
		RegistrationToken token = new RegistrationToken(user);

		// the token value that will be used for the call of
		// activateUser(tokenValue)
		String tokenValue = token.getToken();

		// mock the registrationTokenService
		when(registrationTokenService.findByTokenValue(tokenValue)).thenReturn(token);
		doNothing().when(registrationTokenService).validateToken(token);
		doNothing().when(registrationTokenService).deleteTokenAfterActivation(token);

		//mock the role service
		final String defaultUserRoleName = "ROLE_USER";
		when(defaultUserRole.getName()).thenReturn(defaultUserRoleName);
		when(roleService.findByRoleName(defaultUserRoleName)).thenReturn(defaultUserRole);

		// mock the dao
		doNothing().when(dao).saveOrUpdate(any(User.class));

		// be sure that the user is not active before activating
		assertFalse(user.isActive());

		// finally call the method that is tested here
		crudService.activateUser(tokenValue);

		// check first if user is active now
		assertTrue(user.isActive());

		// check if user has at least one role
		assertFalse(user.getRoles().isEmpty());

		// verify method invocations
		verify(registrationTokenService, times(1)).findByTokenValue(tokenValue);
		verify(registrationTokenService, times(1)).validateToken(token);
		verify(registrationTokenService, times(1)).deleteTokenAfterActivation(token);
		verifyNoMoreInteractions(registrationTokenService);

		verify(defaultUserRole, times(1)).getName();
		verifyNoMoreInteractions(defaultUserRole);

		verify(roleService, times(1)).findByRoleName(defaultUserRoleName);
		verifyNoMoreInteractions(roleService);

		verify(dao, times(1)).saveOrUpdate(any(User.class));
		verifyNoMoreInteractions(dao);

	}

	@Test
	public void activateUser_shouldThrowExceptionIfTokenCouldNotBeValidated() throws Exception {
		// an inactive user that is assigend to a token
		User user = new User();
		user.setActive(false);

		// create a token that is associated with the user
		RegistrationToken token = new RegistrationToken(user);

		// the token value that will be used for the call of
		// activateUser(tokenValue)
		String tokenValue = token.getToken();

		// mock the registrationTokenService
		final String expectedErrorMsg = "invalid token";
		when(registrationTokenService.findByTokenValue(tokenValue)).thenReturn(token);
		doThrow(new Exception(expectedErrorMsg)).when(registrationTokenService).validateToken(token);

		// finally call the method that is tested here
		try {
			crudService.activateUser(tokenValue);
			fail("Should have thrown Exception, but did not!");
		} catch (Exception e) {
			final String actualErrorMsg = e.getMessage();
			assertEquals(expectedErrorMsg, actualErrorMsg);

			// verify method invocations
			verify(registrationTokenService, times(1)).findByTokenValue(tokenValue);
			verify(registrationTokenService, times(1)).validateToken(token);
			verifyNoMoreInteractions(registrationTokenService);

			verifyNoMoreInteractions(dao);
		}
	}

	@Test
	public void persistNewUser_shouldPersistAndEncrypt() {

		String rawPassword = "p@sSw0rd";
		boolean encryptPassword = true;

		User unpersistedUser = new User();
		unpersistedUser.setPassword(rawPassword);

		// mock the dao
		doNothing().when(dao).saveOrUpdate(any(User.class));

		// finally call the method that is tested here
		User persistedUser = crudService.persistNewUser(unpersistedUser, encryptPassword);

		// verify method invocations
		verify(dao, times(1)).saveOrUpdate(any(User.class));
		verifyNoMoreInteractions(dao);

		// check if the password has been encrypted
		assertTrue(passwordEncoder.matches(rawPassword, persistedUser.getPassword()));
	}

	@Test
	public void persistNewUser_shouldPersistButNotEncrypt() {

		String password = "p@sSw0rd";
		boolean encryptPassword = false;

		User unpersistedUser = new User();
		unpersistedUser.setPassword(password);

		// mock the dao
		doNothing().when(dao).saveOrUpdate(any(User.class));

		// finally call the method that is tested here
		User persistedUser = crudService.persistNewUser(unpersistedUser, encryptPassword);

		// verify method invocations
		verify(dao, times(1)).saveOrUpdate(any(User.class));
		verifyNoMoreInteractions(dao);

		// verify that the password is the same as before and was not encrypted
		assertEquals(password, persistedUser.getPassword());
	}

	@Test
	public void persistNewUser_doesNothingIfUserHasId() throws NoSuchFieldException, IllegalAccessException {

		String rawPassword = "p@sSw0rd";
		boolean encryptPassword = true;
		Integer userId = 42;

		User unpersistedUser = new User("Dummy", "User", "dummyuser");
		unpersistedUser.setPassword(rawPassword);

		IdHelper.setIdOnPersistentObject(unpersistedUser, userId);

		// finally call the method that is tested here
		User persistedUser = crudService.persistNewUser(unpersistedUser, encryptPassword);

		// verify method invocations
		verifyNoMoreInteractions(dao);

		// verify that nothing else happened (i.e. no password encryption)
		assertEquals(unpersistedUser, persistedUser);
	}

	@Test
	public void updatePassword_shouldUpdatePasswordAsExpected() throws Exception {

		String oldPassword = "eNcrYpt3dOldP4ssw0rd";
		String newPassword = "r4Wn3Wp@sSw0rd";
		Integer userId = 42;

		User user = new User();
		user.setPassword(oldPassword);

		IdHelper.setIdOnPersistentObject(user, userId );

		// mock the dao
		doNothing().when(dao).saveOrUpdate(any(User.class));

		// finally call the method that is tested here
		crudService.updatePassword(user, newPassword);

		// verify method invocations
		verify(dao, times(1)).saveOrUpdate(any(User.class));
		verifyNoMoreInteractions(dao);

		// verify password
		String encryptedNewPassword = user.getPassword();

		assertFalse(oldPassword.equals(encryptedNewPassword));
		assertTrue(passwordEncoder.matches(newPassword, encryptedNewPassword));
	}

	@Test
	public void updatePassword_shouldThrowIfUserHasNoId() {

		String newPassword = "r4Wn3Wp@sSw0rd";

		// user without id
		User user = new User();

		// call the method that is tested here
		try {
			crudService.updatePassword(user, newPassword);
			fail("Should have thrown Exception, but did not!");
		} catch (Exception e) {

			assertEquals("The ID of the user object is null.", e.getMessage());

			// verify method invocations
			verifyNoMoreInteractions(dao);
		}
	}

}
