package de.terrestris.shogun2.service;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.PasswordResetToken;

/**
 * Service class for the {@link User} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("userService")
public class UserService extends AbstractExtDirectCrudService<User> {

	/**
	 * The Logger
	 */
	private static final Logger LOG =
			Logger.getLogger(UserService.class);

	/**
	 * The autowired PasswordEncoder
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 *
	 */
	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	/**
	 * Returns the user for the given (unique) account name.
	 * If no user was found, null will be returned.
	 *
	 * @param accountName A unique account name.
	 * @return The unique user for the account name or null.
	 */
	public User findByAccountName(String accountName) {

		SimpleExpression eqAccountName = Restrictions.eq("accountName",
				accountName);
		User user = dao.findByUniqueCriteria(eqAccountName);

		return user;
	}

	/**
	 *
	 * @param email
	 * @return
	 */
	public User findByEmail(String email) {

		SimpleExpression eqEmail = Restrictions.eq("email",
				email);
		User user = dao.findByUniqueCriteria(eqEmail);

		return user;
	}

	/**
	 * Persists a new user in the database.
	 *
	 * @param user
	 *            The user to create
	 * @param encryptPassword
	 *            Whether or not the current password of the user object should
	 *            be encrypted or not before the object is persisted in the db
	 *
	 * @return The persisted user object (incl. ID value)
	 */
	public User createNewUser(User user, boolean encryptPassword) {

		if(user.getId() != null) {
			// to be sure that we are in the
			// "create" case, the id must be null
			return user;
		}

		if(encryptPassword){
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		dao.saveOrUpdate(user);

		return user;
	}

	/**
	 *
	 * @param email
	 * @return
	 */
	public Boolean resetPassword(String email) throws
			UsernameNotFoundException, Exception {

		Boolean success = false;

		// get the user (by the provided email address)
		User user = this.findByEmail(email);

		if (user == null) {
			throw new UsernameNotFoundException("Could not find user "
					+ "with email: " + email);
		}

		// generate and save the unique reset password token for the user
		PasswordResetToken resetPasswordToken = passwordResetTokenService
				.generateResetPasswordToken(user);

		if (resetPasswordToken == null) {
			throw new Exception("Error while resetting the password");
		}

		success = true;

		// send mail to user with the given token
//		resetPasswordMailMessageTemplate.setTo(email);
//		resetPasswordMailMessageTemplate.setText(
//				String.format(
//						resetPasswordMailMessageTemplate.getText(),
//						"Username",
//						"http://activate-token.com" + resetPasswordToken.getToken()
//				)
//		);
//		mailPublisher.sendMail(resetPasswordMailMessageTemplate);

		return success;
	}

	/**
	 *
	 * @param id
	 * @param token
	 * @return
	 */
	public Boolean changePassword(int id, String token) {

		Boolean success = false;

		// try to find the provided token
		PasswordResetToken passwordResetToken =
				passwordResetTokenService.findByIdAndToken(id, token);

		// TODO or throw exception?
		if (passwordResetToken == null) {
			LOG.error("Could not find the passwordResetToken");
		}

		DateTime expirationDate = (DateTime) passwordResetToken.getExpirationDate();

		if (expirationDate.isAfterNow()) {
			LOG.error("Token is expired!");
		}

		//TODO let the user change the password!

		return success;
	}

	/**
	 * @return the passwordEncoder
	 */
	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	/**
	 * @param passwordEncoder the passwordEncoder to set
	 */
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * @return the passwordResetTokenService
	 */
	public PasswordResetTokenService getPasswordResetTokenService() {
		return passwordResetTokenService;
	}

	/**
	 * @param passwordResetTokenService the passwordResetTokenService to set
	 */
	public void setPasswordResetTokenService(
			PasswordResetTokenService passwordResetTokenService) {
		this.passwordResetTokenService = passwordResetTokenService;
	}

}
