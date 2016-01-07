package de.terrestris.shogun2.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
 * @author Nils Bühner
 *
 */
@Service("passwordResetTokenService")
public class PasswordResetTokenService extends AbstractUserTokenService<PasswordResetToken> {

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
	@Qualifier("resetPasswordMailMessageTemplate")
	private SimpleMailMessage resetPasswordMailMessageTemplate;

	/**
	 *
	 */
	@Autowired
	private String changePasswordPath;

	/**
	 * @param request
	 * @param email
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	public void sendResetPasswordMail(HttpServletRequest request, String email)
			throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			URISyntaxException, UnsupportedEncodingException {

		// get the user by the provided email address
		User user = userService.findByEmail(email);

		if (user == null) {
			throw new UsernameNotFoundException(
					"Could not find user with email: '" + email + "'");
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

	}

	/**
	 * @param rawPassword
	 * @param token
	 * @throws Exception
	 */
	public void validateTokenAndUpdatePassword(String rawPassword, String token) throws Exception {

		// try to find the provided token
		PasswordResetToken passwordResetToken = findByTokenValue(token);

		// this would throw an exception if the token is not valid
		this.validateToken(passwordResetToken);

		// the user's password can be changed now

		// get the user of the provided token
		User user = passwordResetToken.getUser();

		if (user == null) {
			throw new Exception("Could not find the user for the "
					+ "provided token.");
		}

		// finally update the password (encrypted)
		userService.updatePassword(user, rawPassword);

		// delete the token
		dao.delete(passwordResetToken);

		LOG.trace("Deleted the token.");
		LOG.debug("Successfully updated the password.");

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
