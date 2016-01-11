package de.terrestris.shogun2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;

import org.hibernate.HibernateException;
import org.hibernate.criterion.SimpleExpression;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.UserToken;

/**
 * Test for the {@link AbstractExtDirectCrudService}.
 *
 * @author Nils Bühner
 *
 */
@SuppressWarnings({ "unchecked" })
public abstract class AbstractUserTokenServiceTest<E extends UserToken> {

	/**
	 * Static object that holds concrete instances of
	 * {@link UserToken} for the tests.
	 */
	protected static UserToken userTokenToUse = null;

	@Mock
	protected GenericHibernateDao<E, Integer> dao;

	@InjectMocks
	protected AbstractUserTokenService<E> userTokenService;

	@Before
	public void setUp() {
		this.userTokenService = getUserTokenService();
		// Process mock annotations
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * This method has to be implemented by subclasses.
	 *
	 * @throws Exception
	 */
	@Before
	public abstract void setUpUserTokenToUse() throws Exception;

	/**
	 * This method has to be implemented by subclasses to return a concrete
	 * implementation of the tested service.
	 *
	 * @return
	 */
	protected abstract AbstractUserTokenService<E> getUserTokenService();

	/**
	 * Has to be implemented by subclasses and should return an expired token.
	 *
	 * @throws Exception
	 */
	protected abstract E getExpiredUserToken();

	/**
	 * Has to be implemented by subclasses and should return a token without
	 * associated user.
	 *
	 * @throws Exception
	 */
	protected abstract E getUserTokenWithoutUser();

	@After
	public void tearDownAfterEachTest() throws Exception {
		userTokenToUse = null;
	}

	@Test
	public void findByUser_shouldFindUserToken() {
		User user = new User();

		// mock the dao
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn((E) userTokenToUse);

		E actualUserToken = userTokenService.findByUser(user);

		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verifyNoMoreInteractions(dao);

		assertEquals(actualUserToken, userTokenToUse);
	}

	@Test
	public void findByUser_shouldFindNothing() {
		User userWithoutToken = new User();

		// mock the dao
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(null);

		E actualUserToken = userTokenService.findByUser(userWithoutToken);

		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verifyNoMoreInteractions(dao);

		assertNull(actualUserToken);
	}

	@Test(expected=HibernateException.class)
	public void findByUser_shouldThrowHibernateException() {
		User userThatThrowsException = new User();

		// mock the dao
		doThrow(new HibernateException("errormsg"))
			.when(dao).findByUniqueCriteria(any(SimpleExpression.class));

		userTokenService.findByUser(userThatThrowsException);

		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verifyNoMoreInteractions(dao);
	}

	@Test
	public void validateToken_shouldSuccessfullyValidate() throws Exception {
		userTokenService.validateToken(userTokenToUse);
		assertNotNull(userTokenToUse);
	}

	@Test
	public void validateToken_shouldThrowIfTokenIsNull() {
		try {
			userTokenService.validateToken(null);
			fail("Should have thrown Exception, but did not!");
		} catch (Exception e) {
			final String msg = e.getMessage();
			assertEquals(msg, "The provided token is null.");
		}
	}

	@Test
	public void validateToken_shouldThrowIfTokenExpired() {

		final E expiredUserToken = getExpiredUserToken();

		// mock an expired token
		try {
			userTokenService.validateToken(expiredUserToken);
			fail("Should have thrown Exception, but did not!");
		} catch (Exception e) {
			final String msg = e.getMessage();
			assertEquals(msg, "The token '" + expiredUserToken.getToken()
					+ "' expired on '" + expiredUserToken.getExpirationDate() + "'");
		}
	}

	@Test
	public void validateToken_shouldThrowIfTokenHasNoAssociatedUser() {

		final E expiredUserToken = getUserTokenWithoutUser();

		// mock an expired token
		try {
			userTokenService.validateToken(expiredUserToken);
			fail("Should have thrown Exception, but did not!");
		} catch (Exception e) {
			final String msg = e.getMessage();
			assertEquals(msg, "There is no user associated with this token.");
		}
	}

	@Test
	public void getValidTokenForUser_shouldReturnNewTokenWithDefaultExpirationAsExpected()
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		User user = userTokenToUse.getUser();

		// mock the dao
		// will be called by findByUser -> return null, i.e. there is no
		// existing token for the user
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(null);

		E newUserToken = userTokenService.getValidTokenForUser(user, null);

		// verify dao invocations
		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verify(dao, times(1)).saveOrUpdate((E) any(UserToken.class));
		verifyNoMoreInteractions(dao);

		assertEquals(user, newUserToken.getUser());
	}

	@Test
	public void getValidTokenForUser_shouldReturnNewTokenWithExplicitExpirationAsExpected()
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		User user = userTokenToUse.getUser();

		// mock the dao
		// will be called by findByUser -> return null, i.e. there is no
		// existing token for the user
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(null);

		Integer expirationInMinutes = 120;
		E newUserToken = userTokenService.getValidTokenForUser(user, expirationInMinutes);

		// verify dao invocations
		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verify(dao, times(1)).saveOrUpdate((E) any(UserToken.class));
		verifyNoMoreInteractions(dao);

		assertEquals(user, newUserToken.getUser());
	}

	@Test
	public void getValidTokenForUser_shouldReturnExistingTokenAsExpected()
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		User user = userTokenToUse.getUser();

		// mock the dao
		// will be called by findByUser -> return null, i.e. there is no
		// existing token for the user
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn((E) userTokenToUse);

		E newUserToken = userTokenService.getValidTokenForUser(user, null);

		// verify dao invocations
		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verifyNoMoreInteractions(dao);

		assertEquals(user, newUserToken.getUser());
	}

	@Test
	public void getValidTokenForUser_shouldDeleteExpiredTokenAndReturnNewTokenAsExpected()
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		E expiredToken = getExpiredUserToken();
		User user = expiredToken .getUser();

		// mock the dao
		// will be called by findByUser -> return null, i.e. there is no
		// existing token for the user
		when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn((E) expiredToken);

		E newUserToken = userTokenService.getValidTokenForUser(user, null);

		// verify dao invocations
		verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
		verify(dao, times(1)).delete((E) any(UserToken.class));
		verify(dao, times(1)).saveOrUpdate((E) any(UserToken.class));
		verifyNoMoreInteractions(dao);

		assertEquals(user, newUserToken.getUser());
	}

}
