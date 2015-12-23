package de.terrestris.shogun2.service;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.PasswordResetToken;
import de.terrestris.shogun2.util.application.Shogun2ContextUtil;
import de.terrestris.shogun2.util.mail.MailPublisher;

/**
 *
 * @author Daniel Koch
 *
 */
@Service("passwordResetTokenService")
public class PasswordResetTokenService extends AbstractCrudService<PasswordResetToken> {

	/**
	 * An expiry tolerance in minutes for the creation of a new
	 * {@link PasswordResetToken}. I.e. if a token is requested for a
	 * {@link User} and an there is an existing token that expires within the
	 * minutes configured in this constant, the existing token will be deleted
	 * and a new one will be created.
	 */
	private static final int EXPIRY_TOLERANCE_MINUTES = 5;

	/**
	 * The Logger
	 */
	private static final Logger LOG =
			Logger.getLogger(PasswordResetTokenService.class);

	/**
	 *
	 */
	@Autowired
	private UserService userService;

	/**
	 *
	 */
	@Autowired
	private MailPublisher mailPublisher;

	/**
	 * The autowired PasswordEncoder
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 *
	 */
	@Autowired
	private SimpleMailMessage resetPasswordMailMessageTemplate;

	/**
	 *
	 */
	@Autowired
	private String changePasswordPath;

	/**
	 *
	 * @param user
	 * @return
	 */
	public PasswordResetToken findByUser(User user) {

		SimpleExpression eqUser = Restrictions.eq("user", user);

		PasswordResetToken passwordResetToken =
				dao.findByUniqueCriteria(eqUser);

		return passwordResetToken;
	}

	/**
	 *
	 * @return
	 */
	public PasswordResetToken findByTokenValue(String token) {

		Criterion criteria = Restrictions.eq("token", token);

		PasswordResetToken passwordResetToken =
				dao.findByUniqueCriteria(criteria);

		return passwordResetToken;
	}

	/**
	 *
	 * @param request
	 * @param email
	 * @return
	 * @throws UsernameNotFoundException
	 * @throws Exception
	 * @throws URISyntaxException
	 */
	public Boolean sendResetPasswordMail(HttpServletRequest request, String email) throws
			UsernameNotFoundException, Exception, URISyntaxException {

		Boolean success = false;

		// get the user by the provided email address
		User user = userService.findByEmail(email);

		if (user == null) {
			throw new UsernameNotFoundException("Could not find user "
					+ "with the provided email: " + email);
		}

		// generate and save the unique reset-password token for the user
		PasswordResetToken resetPasswordToken = getValidTokenForUser(user);

		// create the reset-password URI that will be send to the user
		URI resetPasswordURI = createResetPasswordURI(request,
				resetPasswordToken);

		// create a thread safe "copy" of the template message
		SimpleMailMessage resetPwdMsg = new SimpleMailMessage(
				resetPasswordMailMessageTemplate);

		// prepare a personalized mail with the given token
		resetPwdMsg.setTo(email);
		resetPwdMsg.setText(
				String.format(
						resetPwdMsg.getText(),
						user.getFirstName(),
						user.getLastName(),
						UriUtils.decode(resetPasswordURI.toString(), "UTF-8")
				)
		);

		// and send the mail
		mailPublisher.sendMail(resetPwdMsg);

		// we did it!
		success = true;

		return success;
	}

	/**
	 *
	 * @param password
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public Boolean changePassword(String password, String token)
			throws Exception {

		Boolean success = false;

		// try to find the provided token
		PasswordResetToken passwordResetToken = findByTokenValue(token);

		if (passwordResetToken == null) {
			throw new Exception("The provided token is not valid.");
		}

		DateTime expirationDate = (DateTime) passwordResetToken
				.getExpirationDate();

		// check if the token expire date is valid
		if (expirationDate.isBeforeNow()) {
			throw new Exception("The provided token is expired.");
		}

		// the provided token seems to be valid, the user's password can be
		// be changed

		// get the user by the provided token
		User user = passwordResetToken.getUser();

		if (user == null) {
			throw new Exception("Could not find the user for the "
					+ "provided token.");
		}

		// finally update the password (encrypted)
		try {
			user.setPassword(passwordEncoder.encode(password));
			userService.saveOrUpdate(user);
			LOG.debug("Successfully updated the password.");
		} catch(Exception e) {
			throw new Exception("Could not update the password: "
					+ e.getMessage());
		}

		// delete the token
		dao.delete(passwordResetToken);

		// we did it!
		success = true;

		return success;
	}

	/**
	 *
	 * @param request
	 * @param resetPasswordToken
	 * @return
	 * @throws URISyntaxException
	 */
	private URI createResetPasswordURI(HttpServletRequest request,
			PasswordResetToken resetPasswordToken) throws URISyntaxException {

		// get the webapp URI
		URI appURI = Shogun2ContextUtil.getApplicationURIFromRequest(request);

		// build the change-password URI send to the user
		URI tokenURI = new URIBuilder(appURI)
				.setPath(appURI.getPath() + changePasswordPath)
				.setParameter("token", resetPasswordToken.getToken())
				.build();

		return tokenURI;
	}

	/**
	 * Returns a valid (i.e. non-expired) {@link PasswordResetToken} for the
	 * given user. If the user already has an open and valid token, it will be
	 * returned. If the user has an invalid (i.e. (soon-) expired) token, it
	 * will be deleted and a new one will be generated and returned by this
	 * method.
	 *
	 * @param user
	 *            The user that wants to reset the password.
	 * @return A valid (i.e. non-expired) password reset token.
	 * @throws Exception
	 */
	private PasswordResetToken getValidTokenForUser(User user) throws Exception {

		// check if the user has an open reset request / not used token
		PasswordResetToken passwordResetToken = findByUser(user);

		// if there is already an existing token for the user...
		if (passwordResetToken != null) {

			if (passwordResetToken.expiresWithin(EXPIRY_TOLERANCE_MINUTES)) {
				LOG.debug("User already has an expired token (or at least a "
						+ "token that expires within the next "
						+ EXPIRY_TOLERANCE_MINUTES + " minutes). This token "
								+ "will be deleted.");

				// delete the expired token
				dao.delete(passwordResetToken);
			} else {
				// return the existing and valid token
				return passwordResetToken;
			}

		}

		// create a new one
		try {
			passwordResetToken = new PasswordResetToken(user);

			dao.saveOrUpdate(passwordResetToken);

			LOG.debug("Successfully created the reset-password token.");

		} catch(Exception e) {
			throw new Exception("Could not create the reset-password "
					+ "token: " + e.getMessage());
		}

		return passwordResetToken;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * @param userService the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * @return the mailPublisher
	 */
	public MailPublisher getMailPublisher() {
		return mailPublisher;
	}

	/**
	 * @param mailPublisher the mailPublisher to set
	 */
	public void setMailPublisher(MailPublisher mailPublisher) {
		this.mailPublisher = mailPublisher;
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
	 * @return the resetPasswordMailMessageTemplate
	 */
	public SimpleMailMessage getResetPasswordMailMessageTemplate() {
		return resetPasswordMailMessageTemplate;
	}

	/**
	 * @param resetPasswordMailMessageTemplate the resetPasswordMailMessageTemplate to set
	 */
	public void setResetPasswordMailMessageTemplate(
			SimpleMailMessage resetPasswordMailMessageTemplate) {
		this.resetPasswordMailMessageTemplate = resetPasswordMailMessageTemplate;
	}

	/**
	 * @return the changePasswordPath
	 */
	public String getChangePasswordPath() {
		return changePasswordPath;
	}

	/**
	 * @param changePasswordPath the changePasswordPath to set
	 */
	public void setChangePasswordPath(String changePasswordPath) {
		this.changePasswordPath = changePasswordPath;
	}

}
