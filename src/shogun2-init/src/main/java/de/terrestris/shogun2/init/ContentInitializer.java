package de.terrestris.shogun2.init;

import java.sql.SQLException;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.layout.Layout;
import de.terrestris.shogun2.model.module.Module;
import de.terrestris.shogun2.security.acl.AclUtil;
import de.terrestris.shogun2.service.InitializationService;

/**
 * Class to initialize some kind of content.
 *
 * <b>ATTENTION:</b> This class is currently used to provide some demo content.
 * In future, certain entities (like some default {@link Layout}s or
 * {@link Module} s should be created on the base of (configurable) bean
 * definitions.
 *
 * @author Nils Bühner
 *
 */
public class ContentInitializer {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger.getLogger(ContentInitializer.class);

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
	 * Flag symbolizing if a set of default users should be created
	 * up on startup. This will only happen if {@link #shogunInitEnabled} is true.
	 */
	@Autowired
	@Qualifier("createDefaultUsers")
	private Boolean createDefaultUsers;

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
	 * A set of default users that will be created
	 * if {@link #createDefaultUsers} is true.
	 *
	 * Using the {@link Resource} annotation as
	 * recommended on http://stackoverflow.com/a/22463219
	 */
	@Resource(name = "defaultUsers")
	private Set<User> defaultUsers;

	/**
	 * The method called on initialization
	 *
	 * THIS WILL CURRENTLY PRODUCE SOME DEMO CONTENT
	 */
	public void initializeDatabaseContent() {

		if (this.shogunInitEnabled) {

			LOG.info("Initializing some SHOGun2 demo content!");

			if(this.cleanupAclTables) {
				cleanupAclTables();
			}

			if(this.createDefaultUsers) {
				createDefaultUsers();
			}

//			// MANAGE SECURITY/ACL
//
//			logInUser(admin);
//
//			aclSecurityUtil.addPermission(adminApp, admin, BasePermission.READ);
//			aclSecurityUtil.addPermission(userApp, admin, BasePermission.READ);
//			aclSecurityUtil.addPermission(userApp, user, BasePermission.READ);
//
//			LOG.info("Managed security/ACL");
//
//			logoutUser();


		} else {
			LOG.info("Not initializing anything for SHOGun2.");
		}
	}

	/**
	 * Creates the users defined in {@link #defaultUsers}
	 */
	private void createDefaultUsers() {
		LOG.info("Creating a set of default users.");

		for (User user : defaultUsers) {
			initService.createUser(user);
		}

		LOG.info("Created a total of " + defaultUsers.size() + " default users.");
	}

	/**
	 * This method logs in the passed user. (The ACL system needs a user with
	 * ROLE_ADMIN to write ACL entries to the database).
	 *
	 * @param user
	 */
	private void logInUser(User user) {
		Authentication authRequest = new UsernamePasswordAuthenticationToken(user.getAccountName(), user.getPassword());

		Authentication authResult = authenticationProvider.authenticate(authRequest);
		SecurityContextHolder.getContext().setAuthentication(authResult);
	}

	/**
	 * Logs out the current user
	 */
	private void logoutUser() {
		SecurityContextHolder.getContext().setAuthentication(null);
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
