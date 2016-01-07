package de.terrestris.shogun2.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.model.User;

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
	private static final Logger LOG = Logger.getLogger(UserService.class);

	/**
	 * Registration token service
	 */
	@Autowired
	private RegistrationTokenService registrationTokenService;

	/**
	 * The autowired PasswordEncoder
	 */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Returns the user for the given (unique) account name.
	 * If no user was found, null will be returned.
	 *
	 * @param accountName A unique account name.
	 * @return The unique user for the account name or null.
	 */
	public User findByAccountName(String accountName) {

		SimpleExpression eqAccountName =
			Restrictions.eq("accountName", accountName);

		User user = dao.findByUniqueCriteria(eqAccountName);

		return user;
	}

	/**
	 *
	 * @param email
	 * @return
	 */
	public User findByEmail(String email) {

		SimpleExpression eqEmail = Restrictions.eq("email", email);
		User user = dao.findByUniqueCriteria(eqEmail);

		return user;
	}

	/**
	 * Registers a new user
	 *
	 * @param email
	 * @param rawPassword
	 * @param isActive
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public User registerUser(String email, String rawPassword, boolean isActive,
			HttpServletRequest request) throws Exception {

		// check if a user with the email already exists
		User user = this.findByEmail(email);

		if(user != null) {
			final String errorMessage = "User with eMail '" + email + "' already exists.";
			LOG.info(errorMessage);
			throw new Exception(errorMessage);
		}

		user = new User();

		user.setEmail(email);
		user.setAccountName(email);
		user.setPassword(rawPassword);
		user.setActive(isActive);

		user = this.persistNewUser(user, true);

		// create a token for the user and send an email with an "activation" link
		registrationTokenService.sendRegistrationActivationMail(request, user);

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
	public User persistNewUser(User user, boolean encryptPassword) {

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
	 * @param user
	 * @param rawPassword
	 * @throws Exception
	 */
	public void updatePassword(User user, String rawPassword) throws Exception {

		if(user.getId() == null) {
			throw new Exception("The ID of the user object is null.");
		}

		user.setPassword(passwordEncoder.encode(rawPassword));
		dao.saveOrUpdate(user);
	}

	/**
	 * @return the registrationTokenService
	 */
	public RegistrationTokenService getRegistrationTokenService() {
		return registrationTokenService;
	}

	/**
	 * @param registrationTokenService the registrationTokenService to set
	 */
	public void setRegistrationTokenService(
			RegistrationTokenService registrationTokenService) {
		this.registrationTokenService = registrationTokenService;
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

}
