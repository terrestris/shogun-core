package de.terrestris.shogun2.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.init.ContentInitializer;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;

/**
 * This service class will be used by the {@link ContentInitializer} to create content
 * on initialization.
 *
 * @author Nils Bühner
 *
 */
@Service("initializationService")
@Transactional(value="transactionManager")
public class InitializationService {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger
			.getLogger(InitializationService.class);

	/**
	 * A generic dao that can easily be used for any entity that extends
	 * {@link PersistentObject}.
	 */
	@Autowired
	@Qualifier("genericDao")
	private GenericHibernateDao<PersistentObject, Integer> dao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * A "generic" method to save an arbitrary {@link PersistentObject}.
	 *
	 * @param object
	 */
	public void savePersistentObject(PersistentObject object) {
		final String type = object.getClass().getSimpleName();
		LOG.trace("Trying to create a new " + type);
		dao.saveOrUpdate(object);
		LOG.info("Created the " + type + " with id " + object.getId());
	}

	/**
	 * Used to create a user. Implements special logic by encoding the password.
	 *
	 * @param user
	 * @return
	 */
	public void saveUser(User user) {
		LOG.trace("Trying to create a new user");
		// encode the raw password using bcrypt
		final String pwHash = passwordEncoder.encode(user.getPassword());
		user.setPassword(pwHash);
		dao.saveOrUpdate(user);
		LOG.info("Created the user " + user.getAccountName());
	}

}
