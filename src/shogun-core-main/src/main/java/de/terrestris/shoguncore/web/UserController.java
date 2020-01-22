package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.dao.PasswordResetTokenDao;
import de.terrestris.shoguncore.dao.UserDao;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.token.PasswordResetToken;
import de.terrestris.shoguncore.service.PasswordResetTokenService;
import de.terrestris.shoguncore.service.UserService;
import de.terrestris.shoguncore.util.data.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Daniel Koch
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
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public UserController() {
        this((Class<E>) User.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected UserController(Class<E> entityClass) {
        super(entityClass);
    }

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
     * @param email
     * @param password
     */
    @RequestMapping(value = "/register.action", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String, Object> registerUser(HttpServletRequest request,
                                     @RequestParam String email,
                                     @RequestParam String password) {

        try {
            // build the user object that will be passed to the service method
            E user = getEntityClass().newInstance();

            user.setEmail(email);
            user.setAccountName(email);
            user.setPassword(password);
            user.setActive(false);

            user = service.registerUser(user, request);

            return ResultSet.success("You have been registered. "
                + "Please check your mails (" + user.getEmail()
                + ") for further instructions.");
        } catch (Exception e) {
            logger.error("Could not register a new user: " + e.getMessage());
            return ResultSet.error("Could not register a new user.");
        }
    }

    /**
     * @param token
     */
    @RequestMapping(value = "/activate.action", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String, Object> activateUser(@RequestParam String token) {

        try {
            service.activateUser(token);
            return ResultSet.success("Your account has successfully been activated.");
        } catch (Exception e) {
            logger.error("Account could not be activated: " + e.getMessage());
            return ResultSet.error("Account could not be activated.");
        }
    }

    /**
     * @param email
     */
    @RequestMapping(value = "/resetPassword.action", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String, Object> resetPassword(HttpServletRequest request,
                                      @RequestParam(value = "email") String email) {

        logger.debug("Requested to reset the password for '" + email + "'");

        try {
            passwordResetTokenService.sendResetPasswordMail(request, email);
            return ResultSet.success("Password reset has been requested. "
                + "Please check your mails!");
        } catch (Exception e) {
            final String message = e.getMessage();
            logger.error("Could not request a password reset: " + message);
            return ResultSet.error(message);
        }
    }

    /**
     * @param token
     */
    @RequestMapping(value = "/changePassword.action", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String, Object> changePassword(
        @RequestParam(value = "password") String password,
        @RequestParam(value = "token") String token) {

        logger.debug("Requested to change a password for token " + token);

        try {
            passwordResetTokenService.validateTokenAndUpdatePassword(password, token);
            return ResultSet.success("Your password was changed successfully.");

        } catch (Exception e) {
            logger.error("Could not change the password: " + e.getMessage());
            return ResultSet.error("Could not change the password. "
                + "Please contact your administrator.");
        }
    }

    /**
     *
     */
    @RequestMapping(value = "/getUserBySession.action", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    Map<String, Object> getUserBySession() {

        logger.debug("Requested to return the logged in user");

        try {
            return ResultSet.success(service.getUserBySession());
        } catch (Exception e) {
            return ResultSet.error("Could not obtain the user by "
                + "session: " + e.getMessage());
        }
    }

    /**
     * @return the passwordResetTokenService
     */
    public PasswordResetTokenService<PasswordResetToken, PasswordResetTokenDao<PasswordResetToken>> getPasswordResetTokenService() {
        return passwordResetTokenService;
    }

    /**
     * @param passwordResetTokenService the passwordResetTokenService to set
     */
    public void setPasswordResetTokenService(
        PasswordResetTokenService<PasswordResetToken, PasswordResetTokenDao<PasswordResetToken>> passwordResetTokenService) {
        this.passwordResetTokenService = passwordResetTokenService;
    }

}
