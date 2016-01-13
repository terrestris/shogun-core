package de.terrestris.shogun2.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.terrestris.shogun2.dao.PasswordResetTokenDao;
import de.terrestris.shogun2.dao.UserDao;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.PasswordResetToken;
import de.terrestris.shogun2.service.PasswordResetTokenService;
import de.terrestris.shogun2.service.UserService;

/**
 *
 * @author Daniel Koch
 *
 */
@Controller
@RequestMapping("/user")
public class UserController<E extends User, D extends UserDao<E>, S extends UserService<E, D>>
		extends AbstractWebController<E, D, S> {

	/**
	 *
	 */
	@Autowired
	private PasswordResetTokenService<PasswordResetToken, PasswordResetTokenDao<PasswordResetToken>> passwordResetTokenService;

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("userService")
	public void setService(S service) {
		this.service = service;
	}

	/**
	 *
	 * @param email
	 * @param password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/register.action", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> registerUser(HttpServletRequest request,
			@RequestParam String email,
			@RequestParam String password) {

		// build the user object that will be passed to the service method
		E user = (E) new User();

		user.setEmail(email);
		user.setAccountName(email);
		user.setPassword(password);
		user.setActive(false);

		try {
			user = service.registerUser(user, request);

			return this.getModelMapSuccess("You have been registered. "
					+ "Please check your mails (" + user.getEmail()
					+ ") for further instructions.");
		} catch(Exception e) {
			LOG.error("Could not register a new user: " + e.getMessage());
			return this.getModelMapError("Could not register a new user.");
		}
	}

	/**
	 *
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/activate.action", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> activateUser(@RequestParam String token) {

		try {
			service.activateUser(token);
			return this.getModelMapSuccess("Your account has successfully been activated.");
		} catch(Exception e) {
			LOG.error("Account could not be activated: " + e.getMessage());
			return this.getModelMapError("Account could not be activated.");
		}
	}

	/**
	 *
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/resetPassword.action", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> resetPassword(HttpServletRequest request,
			@RequestParam(value = "email") String email) {

		LOG.debug("Requested to reset the password for '" + email + "'");

		try {
			passwordResetTokenService.sendResetPasswordMail(request, email);
			return this.getModelMapSuccess("Password reset has been requested. "
					+ "Please check your mails!");
		} catch (Exception e) {
			LOG.error("Could not request a password reset: " + e.getMessage());
			return this.getModelMapError("An error has occured during passwort reset request.");
		}
	}

	/**
	 *
	 * @param id
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/changePassword.action", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> changePassword(
			@RequestParam(value = "password") String password,
			@RequestParam(value = "token") String token) {

		LOG.debug("Requested to change a password for token " + token);

		try {
			passwordResetTokenService.validateTokenAndUpdatePassword(password, token);
			return this.getModelMapSuccess("Your password was changed successfully.");

		} catch (Exception e) {
			LOG.error("Could not change the password: " + e.getMessage());
			return this.getModelMapError("Could not change the password. "
					+ "Please contact your administrator.");
		}
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value = "/getUserBySession.action", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getUserBySession() {

		LOG.debug("Requested to return the logged in user");

		try {
			return this.getModelMapSuccess(service.getUserBySession());
		} catch (Exception e) {
			return this.getModelMapError("Could not obtain the user by "
					+ "session: " + e.getMessage());
		}
	}

}
