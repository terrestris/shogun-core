package de.terrestris.shogun2.service;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.PasswordResetToken;

/**
 *
 * @author Daniel Koch
 *
 */
@Service("passwordResetTokenService")
public class PasswordResetTokenService extends AbstractCrudService<PasswordResetToken> {

	/**
	 * The Logger
	 */
	private static final Logger LOG =
			Logger.getLogger(PasswordResetTokenService.class);

	/**
	 *
	 * @param user
	 * @return
	 */
	public PasswordResetToken findByUser(User user) {

		SimpleExpression eqEmail = Restrictions.eq("user", user);
		PasswordResetToken passwordResetToken =
				dao.findByUniqueCriteria(eqEmail);

		return passwordResetToken;
	}

	/**
	 *
	 * @return
	 */
	public PasswordResetToken findByIdAndToken(int id, String token) {

		Criterion criteria = Restrictions.and(
				Restrictions.eq("id", id),
				Restrictions.eq("token", token)
		);

		PasswordResetToken passwordResetToken =
				dao.findByUniqueCriteria(criteria);

		return passwordResetToken;
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public PasswordResetToken generateResetPasswordToken(User user) throws Exception {

		PasswordResetToken passwordResetToken;

		// generate the token itself
		String token = UUID.randomUUID().toString();

		// check if the user has an open reset request / not used token
		passwordResetToken = findByUser(user);

		// if it's present, delete it
		if (passwordResetToken != null) {
			LOG.debug("User has an open request already, delete it first");
			dao.delete(passwordResetToken);
		}

		// and try to create a blank new one
		try {
			passwordResetToken = new PasswordResetToken();
			passwordResetToken.setUser(user);
			passwordResetToken.setToken(token);

			dao.saveOrUpdate(passwordResetToken);

			LOG.debug("Successfully created the reset-password token.");

		} catch(Exception e) {
			throw new Exception("Could not create the reset-password "
					+ "token: " + e.getMessage());
		}

		return passwordResetToken;
	}

}
