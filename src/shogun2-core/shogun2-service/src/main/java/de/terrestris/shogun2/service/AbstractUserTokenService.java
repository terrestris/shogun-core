package de.terrestris.shogun2.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.joda.time.DateTime;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.PasswordResetToken;
import de.terrestris.shogun2.model.token.UserToken;

/**
 *
 * @author Daniel Koch
 * @author Nils Bühner
 *
 */
public abstract class AbstractUserTokenService<E extends UserToken> extends AbstractCrudService<E> {

	/**
	 * The Logger
	 */
	private static final Logger LOG =
			Logger.getLogger(AbstractUserTokenService.class);

	/**
	 * An expiry threshold in minutes for the creation of a new
	 * {@link PasswordResetToken}. I.e. if a token is requested for a
	 * {@link User} and an there is an existing token that expires within the
	 * minutes configured in this constant, the existing token will be deleted
	 * and a new one will be created.
	 */
	private static final int EXPIRY_THRESHOLD_MINUTES = 5;

	/**
	 *
	 * @param user
	 * @return
	 */
	public E findByUser(User user) {

		SimpleExpression eqUser = Restrictions.eq("user", user);

		E userToken = dao.findByUniqueCriteria(eqUser);

		return userToken;
	}

	/**
	 *
	 * @return
	 */
	public E findByTokenValue(String token) {

		Criterion criteria = Restrictions.eq("token", token);

		E userToken = dao.findByUniqueCriteria(criteria);

		return userToken;
	}

	/**
	 * If the passed token is null or expired or if there is no user associated
	 * with the token, this method will throw an {@link Exception}.
	 *
	 * @param userToken
	 * @throws Exception
	 *             if the token is not valid (e.g. because it is expired)
	 */
	protected void validateToken(UserToken userToken) throws Exception {

		if (userToken == null) {
			throw new Exception("The provided token does not exist.");
		}

		DateTime expirationDate = (DateTime) userToken
				.getExpirationDate();

		String tokenValue = userToken.getToken();

		// check if the token expire date is valid
		if (expirationDate.isBeforeNow()) {
			throw new Exception("The token '" + tokenValue + "' expired on '"
					+ expirationDate + "'");
		}

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
	 *
	 * An expiration time in minutes can also be passed. If this value is null,
	 * the default value will be used.
	 *
	 * @param user
	 *            The user that needs a token.
	 * @param expirationTimeInMinutes
	 *            The expiration time in minutes. If null, the default value
	 *            will be used.
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

		userToken = buildConcreteUserTokenInstance(user, expirationTimeInMinutes);

		// persist the user token
		dao.saveOrUpdate(userToken);

		final String tokenType = userToken.getClass().getSimpleName();
		LOG.debug("Successfully created a user token of type '" + tokenType
				+ "' for user '" + user.getAccountName() + "'");

		return userToken;
	}

	/**
	 * Uses Java reflection to build a new instance of the correct concrete
	 * {@link UserToken} (subclass) type.
	 *
	 * @param user
	 * @param expirationTimeInMinutes
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	private E buildConcreteUserTokenInstance(User user, Integer expirationTimeInMinutes)
			throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {

		Class<E> concreteUserTokenClass = (Class<E>) ((ParameterizedType) this
				.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

		Constructor<E> c;
		E concreteInstance;

		// get the correct constructor (based on the value of
		// expirationTimeInMinutes) and create a new instance
		if(expirationTimeInMinutes == null) {
			c = concreteUserTokenClass.getConstructor(User.class);
			concreteInstance = c.newInstance(user);
		} else {
			c = concreteUserTokenClass.getConstructor(User.class, int.class);
			concreteInstance = c.newInstance(user, expirationTimeInMinutes);
		}

		return concreteInstance;
	}

}
