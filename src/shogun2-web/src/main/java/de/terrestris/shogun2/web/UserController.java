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

import de.terrestris.shogun2.service.PasswordResetTokenService;

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
	private PasswordResetTokenService passwordResetTokenService;

	/**
	 *
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/resetPassword.action", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> resetPassword(HttpServletRequest request,
			@RequestParam(value = "email") String email) {

		LOG.debug("Requested to reset a password for " + email);

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
			passwordResetTokenService.changePassword(password, token);
			return this.getModelMapSuccess("Your password was changed successfully.");

		} catch (Exception e) {
			LOG.error("Could not change the password: " + e.getMessage());
			return this.getModelMapError("Could not change the password. "
					+ "Please contact your administrator.");
		}
	}
}
