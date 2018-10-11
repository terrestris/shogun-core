package de.terrestris.shogun2.service;

import de.terrestris.shogun2.dao.AbstractUserTokenDao;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.UserToken;
import org.hibernate.HibernateException;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test for the {@link AbstractUserTokenService}.
 *
 * @author Nils Bühner
 */
public abstract class AbstractUserTokenServiceTest<E extends UserToken, D extends AbstractUserTokenDao<E>, S extends AbstractUserTokenService<E, D>>
    extends PermissionAwareCrudServiceTest<E, D, S> {

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

    /**
     * This method has to be implemented by subclasses to return the concrete
     * class of the dao.
     *
     * @return
     */
    protected abstract Class<D> getDaoClass();

    @Test
    public void findByUser_shouldFindUserToken() {
        User user = new User();

        // mock the dao
        when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(implToTest);

        E actualUserToken = crudService.findByUser(user);

        verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
        verifyNoMoreInteractions(dao);

        assertEquals(actualUserToken, implToTest);
    }

    @Test
    public void findByUser_shouldFindNothing() {
        User userWithoutToken = new User();

        // mock the dao
        when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(null);

        E actualUserToken = crudService.findByUser(userWithoutToken);

        verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
        verifyNoMoreInteractions(dao);

        assertNull(actualUserToken);
    }

    @Test(expected = HibernateException.class)
    public void findByUser_shouldThrowHibernateException() {
        User userThatThrowsException = new User();

        // mock the dao
        doThrow(new HibernateException("errormsg"))
            .when(dao).findByUniqueCriteria(any(SimpleExpression.class));

        crudService.findByUser(userThatThrowsException);

        verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void validateToken_shouldSuccessfullyValidate() throws Exception {
        crudService.validateToken(implToTest);
        assertNotNull(implToTest);
    }

    @Test
    public void validateToken_shouldThrowIfTokenIsNull() {
        try {
            crudService.validateToken(null);
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
            crudService.validateToken(expiredUserToken);
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
            crudService.validateToken(expiredUserToken);
            fail("Should have thrown Exception, but did not!");
        } catch (Exception e) {
            final String msg = e.getMessage();
            assertEquals(msg, "There is no user associated with this token.");
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getValidTokenForUser_shouldReturnNewTokenWithDefaultExpirationAsExpected()
        throws NoSuchMethodException, SecurityException,
        InstantiationException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {

        User user = implToTest.getUser();

        // mock the dao
        // will be called by findByUser -> return null, i.e. there is no
        // existing token for the user
        when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(null);

        E newUserToken = crudService.getValidTokenForUser(user, null);

        // verify dao invocations
        verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
        verify(dao, times(1)).saveOrUpdate((E) any(UserToken.class));
        verifyNoMoreInteractions(dao);

        assertEquals(user, newUserToken.getUser());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getValidTokenForUser_shouldReturnNewTokenWithExplicitExpirationAsExpected()
        throws NoSuchMethodException, SecurityException,
        InstantiationException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {

        User user = implToTest.getUser();

        // mock the dao
        // will be called by findByUser -> return null, i.e. there is no
        // existing token for the user
        when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(null);

        Integer expirationInMinutes = 120;
        E newUserToken = crudService.getValidTokenForUser(user, expirationInMinutes);

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

        User user = implToTest.getUser();

        // mock the dao
        // will be called by findByUser -> return null, i.e. there is no
        // existing token for the user
        when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn(implToTest);

        E newUserToken = crudService.getValidTokenForUser(user, null);

        // verify dao invocations
        verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
        verifyNoMoreInteractions(dao);

        assertEquals(user, newUserToken.getUser());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getValidTokenForUser_shouldDeleteExpiredTokenAndReturnNewTokenAsExpected()
        throws NoSuchMethodException, SecurityException,
        InstantiationException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {

        E expiredToken = getExpiredUserToken();
        User user = expiredToken.getUser();

        // mock the dao
        // will be called by findByUser -> return null, i.e. there is no
        // existing token for the user
        when(dao.findByUniqueCriteria(any(SimpleExpression.class))).thenReturn((E) expiredToken);

        E newUserToken = crudService.getValidTokenForUser(user, null);

        // verify dao invocations
        verify(dao, times(1)).findByUniqueCriteria(any(SimpleExpression.class));
        verify(dao, times(1)).delete((E) any(UserToken.class));
        verify(dao, times(1)).saveOrUpdate((E) any(UserToken.class));
        verifyNoMoreInteractions(dao);

        assertEquals(user, newUserToken.getUser());
    }

}
