package de.terrestris.shogun2.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.ApplicationDao;
import de.terrestris.shogun2.dao.UserDao;
import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.model.User;

/**
 * This service class will be used by the ContentInitializer to create content
 * on initialization. The methods of this service are not secured, which is
 * required, because otherwise the ACL mechanism would deny access to the
 * secured methods of the AbstractCrudService.
 * 
 * @author Nils Bühner
 * 
 */
@Service("initializationService")
@Transactional
public class InitializationService {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger
			.getLogger(InitializationService.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private ApplicationDao applicationDao;

	/**
	 * Used to create a user.
	 * 
	 * @param user
	 * @return
	 */
	public User createUser(User user) {
		userDao.saveOrUpdate(user);
		LOG.debug("Created the user " + user);
		return user;
	}

	/**
	 * Used to create an application.
	 * 
	 * @param application
	 * @return
	 */
	public Application createApplication(Application application) {
		applicationDao.saveOrUpdate(application);
		LOG.debug("Created the application " + application);
		return application;
	}

}
