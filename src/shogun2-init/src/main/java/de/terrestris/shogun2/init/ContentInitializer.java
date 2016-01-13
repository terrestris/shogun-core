package de.terrestris.shogun2.init;

import java.sql.SQLException;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.model.Role;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.model.layout.Layout;
import de.terrestris.shogun2.model.module.Module;
import de.terrestris.shogun2.security.acl.AclUtil;
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
	 * Flag symbolizing if the ACL data should be cleaned up on startup. This
	 * will only happen if {@link #shogunInitEnabled} is true.
	 */
	@Autowired
	@Qualifier("cleanupAclTables")
	private Boolean cleanupAclTables;

	/**
	 * The path to the acl cleanup script.
	 */
	@Autowired
	@Qualifier("cleanupAclTablesScriptPath")
	private String cleanupAclTablesScriptPath;

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
	 * The aclDataSource, which is needed to clean up the ACL data.
	 */
	@Autowired
	@Qualifier("aclDataSource")
	protected DataSource aclDataSource;

	/**
	 * The AclSecurityUtil to add/delete permissions.
	 */
	@Autowired
	protected AclUtil aclSecurityUtil;

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

			// TODO: get smarter here
			// determine the admin and remember the rawPassword because this
			// is needed for later authentication of the admin user
			User adminUser = null;
			String rawAdminPassword = null;
			for (User user : defaultUsers) {
				if(user.getAccountName().equals("admin")) {
					adminUser = user;
					rawAdminPassword = user.getPassword();
				}
			}

			if(cleanupAclTables) {
				cleanupAclTables();
			}

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

			// MANAGE SECURITY/ACL
			if(adminUser != null) {

				logInUser(adminUser, rawAdminPassword);

				for (Application application : defaultApplications) {
					aclSecurityUtil.addPermission(application, adminUser, BasePermission.READ);
				}

				LOG.info("Managed security/ACL");

				logoutUser();
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

	/**
	 * This method logs in the passed user. (The ACL system requires an
	 * authenticated user with a satisfying authority (based on the config of
	 * the {@link AclAuthorizationStrategyImpl}) to write ACL entries to the
	 * database.
	 *
	 * @param user
	 * @param rawPassword
	 */
	private void logInUser(User user, String rawPassword) {
		Authentication authRequest = new UsernamePasswordAuthenticationToken(user.getAccountName(), rawPassword);

		Authentication authResult = authenticationProvider.authenticate(authRequest);
		SecurityContextHolder.getContext().setAuthentication(authResult);
	}

	/**
	 * Logs out the current user
	 */
	private void logoutUser() {
		SecurityContextHolder.clearContext();
	}

	/**
	 * Calls a SQL script that deletes all entries from the four ACL tables.
	 */
	private void cleanupAclTables() {
		final ResourceDatabasePopulator rdp = new ResourceDatabasePopulator(
				new ClassPathResource(cleanupAclTablesScriptPath));

		LOG.info("Trying to clean up ACL tables.");

		try {
			rdp.populate(aclDataSource.getConnection());
			LOG.info("Cleaned up ACL tables.");
		} catch (ScriptException | SQLException e) {
			LOG.error("Could not clean up ACL tables: " + e.getMessage());
		}
	}
}
