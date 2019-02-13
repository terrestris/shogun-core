package de.terrestris.shogun2.service;

import java.lang.reflect.InvocationTargetException;

import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.AbstractUserTokenDao;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.PasswordResetToken;
import de.terrestris.shogun2.model.token.UserToken;

/**
 * @author Daniel Koch
 * @author Nils Bühner
 */
public abstract class AbstractUserTokenService<E extends UserToken, D extends AbstractUserTokenDao<E>>
    extends AbstractTokenService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public AbstractUserTokenService() {
        this((Class<E>) UserToken.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected AbstractUserTokenService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * An expiry threshold in minutes for the creation of a new
     * {@link PasswordResetToken}. I.e. if a token is requested for a
     * {@link User} and an there is an existing token that expires within the
     * minutes configured in this constant, the existing token will be deleted
     * and a new one will be created.
     */
    private static final int EXPIRY_THRESHOLD_MINUTES = 5;

    /**
     * Has to be implemented by subclasses to return a concrete instance for the
     * given values. Should return an instance with a default expiration time
     * if expirationTimeInMinutes is null.
     *
     * @param user
     * @param expirationTimeInMinutes
     * @return
     */
    protected abstract E buildConcreteInstance(User user, Integer expirationTimeInMinutes);

    /**
     * @param user
     * @return
     */
    @Transactional(readOnly = true)
    public E findByUser(User user) {

        SimpleExpression eqUser = Restrictions.eq("user", user);

        E userToken = dao.findByUniqueCriteria(eqUser);

        return userToken;
    }

    /**
     * If the passed token is null or expired or if there is no user associated
     * with the token, this method will throw an {@link Exception}.
     *
     * @param userToken
     * @throws Exception if the token is not valid (e.g. because it is expired)
     */
    @Override
    @Transactional(readOnly = true)
    public void validateToken(E userToken) throws Exception {

        // call super
        super.validateToken(userToken);

        // check if user is associated
        if (userToken.getUser() == null) {
            throw new Exception("There is no user associated with this token.");
        }

    }

    /**
     * Returns a valid (i.e. non-expired) {@link UserToken} for the given user.
     * If the user already owns a valid token, it will be returned. If the user
     * has an invalid/expired token, it will be deleted and a new one will be
     * generated and returned by this method.
     * <p>
     * An expiration time in minutes can also be passed. If this value is null,
     * the default value will be used.
     *
     * @param user                    The user that needs a token.
     * @param expirationTimeInMinutes The expiration time in minutes. If null, the default value
     *                                will be used.
     * @return A valid user token.
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected E getValidTokenForUser(User user, Integer expirationTimeInMinutes) throws NoSuchMethodException,
        SecurityException, InstantiationException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {

        // check if the user has an open reset request / not used token
        E userToken = findByUser(user);

        // if there is already an existing token for the user...
        if (userToken != null) {

            if (userToken.expiresWithin(EXPIRY_THRESHOLD_MINUTES)) {
                LOG.debug("User already has an expired token (or at least a "
                    + "token that expires within the next "
                    + EXPIRY_THRESHOLD_MINUTES + " minutes). This token "
                    + "will be deleted.");

                // delete the expired token
                dao.delete(userToken);
            } else {
                LOG.debug("Returning existing token for user '"
                    + user.getAccountName() + "'");
                // return the existing and valid token
                return userToken;
            }

        }

        userToken = buildConcreteInstance(user, expirationTimeInMinutes);

        // persist the user token
        dao.saveOrUpdate(userToken);

        final String tokenType = userToken.getClass().getSimpleName();
        LOG.debug("Successfully created a user token of type '" + tokenType
            + "' for user '" + user.getAccountName() + "'");

        return userToken;
    }

}
