package de.terrestris.shogun2.web;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/resetPassword.action", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> resetPassword(
			@RequestParam(value = "email") String email) {

		LOG.info("Requested to reset a password.");

		try {
			Boolean success = userService.resetPassword(email);
			if (success) {
				return this.getModelMapSuccess("Your password has been reset. "
						+ "Please check your mails!");
			} else {
				return this.getModelMapError("Could not reset the password.");
			}
		} catch(Exception e) {
			LOG.error("Could not reset the password: " + e.getMessage());
			return this.getModelMapError(e.getMessage());
		}
	}

	/**
	 *
	 * @param id
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/changePassword.action", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> changePassword(
			@RequestParam(value = "id") int id,
			@RequestParam(value = "token") String token) {

		LOG.info("Requested to change a password.");

		try {
			Boolean success = userService.changePassword(id, token);
			if (success) {
				return this.getModelMapSuccess("Your password has been changed!");
			} else {
				return this.getModelMapError("Could not change the password.");
			}
		} catch(Exception e) {
			LOG.error("Could not change the password: " + e.getMessage());
			return this.getModelMapError(e.getMessage());
		}
	}
}
