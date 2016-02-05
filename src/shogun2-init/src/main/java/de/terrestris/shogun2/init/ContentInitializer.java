package de.terrestris.shogun2.init;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;

import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.model.Role;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.model.layout.Layout;
import de.terrestris.shogun2.model.module.Module;
import de.terrestris.shogun2.service.InitializationService;

/**
 * Class to initialize an initial set of content based on bean definitions.
 *
 * @author Nils Bühner
 *
 */
public class ContentInitializer {

	/**
	 * The Logger
	 */
	protected final Logger LOG = Logger.getLogger(getClass());

	/**
	 * Flag symbolizing if content initialization should be active on startup
	 */
	@Autowired
	@Qualifier("shogunInitEnabled")
	private Boolean shogunInitEnabled;

	/**
	 * Flag symbolizing if a set of default {@link Role}s should be created
	 * up on startup. This will only happen if {@link #shogunInitEnabled} is true.
	 */
	@Autowired
	@Qualifier("createDefaultRoles")
	private Boolean createDefaultRoles;

	/**
	 * Flag symbolizing if a set of default {@link User}s should be created
	 * up on startup. This will only happen if {@link #shogunInitEnabled} is true.
	 */
	@Autowired
	@Qualifier("createDefaultUsers")
	private Boolean createDefaultUsers;

	/**
	 * Flag symbolizing if a set of default {@link UserGroup}s should be created
	 * up on startup. This will only happen if {@link #shogunInitEnabled} is true.
	 */
	@Autowired
	@Qualifier("createDefaultUserGroups")
	private Boolean createDefaultUserGroups;

	/**
	 * Flag symbolizing if a set of default {@link Layout}s should be created
	 * up on startup. This will only happen if {@link #shogunInitEnabled} is true.
	 */
	@Autowired
	@Qualifier("createDefaultLayouts")
	private Boolean createDefaultLayouts;

	/**
	 * Flag symbolizing if a set of default {@link Module}s should be created
	 * up on startup. This will only happen if {@link #shogunInitEnabled} is true.
	 */
	@Autowired
	@Qualifier("createDefaultModules")
	private Boolean createDefaultModules;

	/**
	 * Flag symbolizing if a set of default {@link Application}s should be created
	 * up on startup. This will only happen if {@link #shogunInitEnabled} is true.
	 */
	@Autowired
	@Qualifier("createDefaultApplications")
	private Boolean createDefaultApplications;

	/**
	 * Initialization Service to init shogun content like users or default
	 * applications.
	 */
	@Autowired
	protected InitializationService initService;

	/**
	 * We use the authenticationProvider to login with the admin user, that will
	 * be created in this initializer.
	 */
	@Autowired
	@Qualifier("shogun2AuthenticationProvider")
	protected AuthenticationProvider authenticationProvider;

	/**
	 * A set of default roles that will be created
	 * if {@link #createDefaultRoles} is true.
	 *
	 */
	@Autowired(required = false)
	private Set<Role> defaultRoles;

	/**
	 * A set of default users that will be created
	 * if {@link #createDefaultUsers} is true.
	 *
	 */
	@Autowired(required = false)
	private Set<User> defaultUsers;

	/**
	 * A set of default userGroups that will be created
	 * if {@link #createDefaultUserGroups} is true.
	 *
	 */
	@Autowired(required = false)
	private Set<UserGroup> defaultUserGroups;

	/**
	 * A set of default layouts that will be created
	 * if {@link #createDefaultLayouts} is true.
	 *
	 */
	@Autowired(required = false)
	private Set<Layout> defaultLayouts;

	/**
	 * A set of default modules that will be created
	 * if {@link #createDefaultModules} is true.
	 *
	 */
	@Autowired(required = false)
	private Set<Module> defaultModules;

	/**
	 * A set of default applications that will be created
	 * if {@link #createDefaultApplications} is true.
	 *
	 */
	@Autowired(required = false)
	private Set<Application> defaultApplications;

	/**
	 * The method called on initialization
	 *
	 */
	public void initializeDatabaseContent() {

		if (this.shogunInitEnabled) {

			LOG.info("Initializing SHOGun2 content");

			if(createDefaultRoles) {
				createDefaultRoles();
			}

			if(createDefaultUsers) {
				createDefaultUsers();
			}

			if(createDefaultUserGroups) {
				createDefaultUserGroups();
			}

			if(createDefaultLayouts) {
				createDefaultLayouts();
			}

			if(createDefaultModules) {
				createDefaultModules();
			}

			if(createDefaultApplications) {
				createDefaultApplications();
			}

		} else {
			LOG.info("Not initializing anything for SHOGun2.");
		}
	}

	/**
	 * Creates the {@link User}s defined in {@link #defaultUsers}
	 */
	private void createDefaultRoles() {
		LOG.info("Creating a set of default roles.");

		for (Role role : defaultRoles) {
			initService.createRole(role);
		}

		LOG.info("Created a total of " + defaultRoles.size() + " default roles.");
	}

	/**
	 * Creates the {@link User}s defined in {@link #defaultUsers}
	 */
	private void createDefaultUsers() {
		LOG.info("Creating a set of default users.");

		for (User user : defaultUsers) {
			initService.createUser(user);
		}

		LOG.info("Created a total of " + defaultUsers.size() + " default users.");
	}

	/**
	 * Creates the {@link UserGroup}s defined in {@link #defaultUserGroups}
	 */
	private void createDefaultUserGroups() {
		LOG.info("Creating a set of default user groups.");

		for (UserGroup userGroup : defaultUserGroups) {
			initService.createUserGroup(userGroup);
		}

		LOG.info("Created a total of " + defaultUserGroups.size() + " default user groups.");
	}

	/**
	 * Creates the {@link Layout}s defined in {@link #defaultLayouts}
	 */
	private void createDefaultLayouts() {
		LOG.info("Creating a set of default layouts.");

		for (Layout layout : defaultLayouts) {
			initService.createLayout(layout);
		}

		LOG.info("Created a total of " + defaultLayouts.size() + " default layouts.");
	}

	/**
	 * Creates the {@link Module}s defined in {@link #defaultApplications}
	 */
	private void createDefaultModules() {
		LOG.info("Creating a set of default modules.");

		for (Module module : defaultModules) {
			initService.createModule(module);
		}

		LOG.info("Created a total of " + defaultModules.size() + " default modules.");
	}

	/**
	 * Creates the {@link Application}s defined in {@link #defaultApplications}
	 */
	private void createDefaultApplications() {
		LOG.info("Creating a set of default applications.");

		for (Application app : defaultApplications) {
			initService.createApplication(app);
		}

		LOG.info("Created a total of " + defaultApplications.size() + " default applications.");
	}

}
