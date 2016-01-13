package de.terrestris.shogun2.service;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import de.terrestris.shogun2.dao.AbstractTokenDao;
import de.terrestris.shogun2.model.token.Token;

/**
 *
 * @author Daniel Koch
 * @author Nils Bühner
 *
 */
public abstract class AbstractTokenService<E extends Token, D extends AbstractTokenDao<E>>
		extends AbstractCrudService<E, D> {

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
	 * If the passed token is null or expired, this method will throw an
	 * {@link Exception}.
	 *
	 * @param token
	 * @throws Exception
	 *             if the token is not valid (e.g. because it is expired)
	 */
	public void validateToken(E token) throws Exception {

		if (token == null) {
			throw new Exception("The provided token is null.");
		}

		DateTime expirationDate = (DateTime) token.getExpirationDate();

		String tokenValue = token.getToken();

		// check if the token expire date is valid
		if (expirationDate.isBeforeNow()) {
			throw new Exception("The token '" + tokenValue + "' expired on '"
					+ expirationDate + "'");
		}

	}

}
