package de.terrestris.shogun2.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.service.PasswordResetTokenService;
import de.terrestris.shogun2.service.UserService;

/**
 *
 * @author Daniel Koch
 *
 */
@Controller
@RequestMapping("/user")
public class UserController extends AbstractWebController {

	/**
	 * The Logger
	 */
	private static final Logger LOG =
			Logger.getLogger(UserController.class);

	/**
	 *
	 */
	@Autowired
	private UserService userService;

	/**
	 *
	 */
	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	/**
	 *
	 * @param email
	 * @param password
	 * @return
	 */
	@RequestMapping(value = "/register.action", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> registerUser(HttpServletRequest request,
			@RequestParam String email,
			@RequestParam String password) {

		try {
			User user = userService.registerUser(email, password, false, request);
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
}
