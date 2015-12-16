package de.terrestris.shogun2.service;

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
	private static final Logger LOG =
			Logger.getLogger(UserService.class);

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
	 * @return
	 */
	public User updateExistingUser(User user) {

		if(user.getId() == null) {
			// to be sure that we are in the
			// "update" case, the id must not be null
			return user;
		}

		dao.saveOrUpdate(user);

		return user;

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
