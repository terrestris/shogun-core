package de.terrestris.shogun2.service;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
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
import de.terrestris.shogun2.util.application.Shogun2ServletContext;
import de.terrestris.shogun2.util.mail.MailPublisher;

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
	private Shogun2ServletContext contextUtil;

	/**
	 *
	 */
	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	/**
	 *
	 */
	@Autowired
	private MailPublisher mailPublisher;

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
	 * @param request
	 * @param email
	 * @return
	 */
	public Boolean resetPassword(HttpServletRequest request, String email) throws
			UsernameNotFoundException, Exception, URISyntaxException {

		Boolean success = false;

		// get the user by the provided email address
		User user = findByEmail(email);

		if (user == null) {
			throw new UsernameNotFoundException("Could not find user "
					+ "with the provided email: " + email);
		}

		// generate and save the unique reset-password token for the user
		PasswordResetToken resetPasswordToken = passwordResetTokenService
				.generateResetPasswordToken(user);

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
	 * @param id
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public Boolean changePassword(String password, int id, String token)
			throws Exception {

		Boolean success = false;

		// try to find the provided token
		PasswordResetToken passwordResetToken =
				passwordResetTokenService.findByIdAndToken(id, token);

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
			dao.saveOrUpdate(user);
			LOG.debug("Successfully updated the password.");
		} catch(Exception e) {
			throw new Exception("Could not update the password: "
					+ e.getMessage());
		}

		// TODO: delete the token
//		passwordResetTokenService.delete(passwordResetToken);

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
	public URI createResetPasswordURI(HttpServletRequest request,
			PasswordResetToken resetPasswordToken) throws URISyntaxException {

		// get the webapp URI
		URI appURI = contextUtil.getApplicationURIFromRequest(request);

		// build the change-password URI send to the user
		URI tokenURI = new URIBuilder()
				.setScheme(appURI.getScheme())
				.setHost(appURI.getHost())
				.setPort(appURI.getPort())
				.setPath(appURI.getPath() + changePasswordPath)
				.setParameter("id", String.valueOf(resetPasswordToken.getId()))
				.setParameter("token", resetPasswordToken.getToken())
				.build();

		return tokenURI;
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
	 * @return the contextUtil
	 */
	public Shogun2ServletContext getContextUtil() {
		return contextUtil;
	}

	/**
	 * @param contextUtil the contextUtil to set
	 */
	public void setContextUtil(Shogun2ServletContext contextUtil) {
		this.contextUtil = contextUtil;
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
