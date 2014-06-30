package de.terrestris.shogun2.init;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.security.acl.AclSecurityUtil;
import de.terrestris.shogun2.service.InitializationService;

/**
 * @author Nils Bühner
 * 
 *         Class to initialize some kind of content
 * 
 */
public class ContentInitializer {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger
			.getLogger(ContentInitializer.class);

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
	@Qualifier("deleteAclData")
	private Boolean deleteAclData;

	/**
	 * The path to the acl cleanup script.
	 */
	@Autowired
	@Qualifier("aclCleanupScriptPath")
	private String aclCleanupScriptPath;

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
	protected AclSecurityUtil aclSecurityUtil;

	/**
	 * We use the authenticationProvider to login with the admin user, that will
	 * be created in this initializer.
	 */
	@Autowired
	@Qualifier("shogun2AuthenticationProvider")
	protected AuthenticationProvider authenticationProvider;

	/**
	 * The method called on initialization
	 */
	public void initializeDatabaseContent() {

		if (this.shogunInitEnabled.equals(true)) {
			LOG.info("Initializing some SHOGun content!");

			LOG.info("Cleaning up ACL tables...");
			cleanupAclTables();

			// CREATE USERS
			User admin = new User("System", "Admin", "admin", "admin", true);
			User user = new User("System", "User", "user", "user", true);

			initService.createUser(admin);
			initService.createUser(user);

			LOG.info("Created an admin and a default user.");

			// CREATE APPS
			Application adminApp = new Application("AdminApp", null);
			Application userApp = new Application("UserApp", null);

			adminApp = initService.createApplication(adminApp);
			userApp = initService.createApplication(userApp);

			LOG.info("Created an admin app and a user app.");

			// MANAGE SECURITY/ACL

			logInUser(admin);

			aclSecurityUtil.addPermission(adminApp, admin, BasePermission.READ);
			aclSecurityUtil.addPermission(userApp, admin, BasePermission.READ);
			aclSecurityUtil.addPermission(userApp, user, BasePermission.READ);

			LOG.info("Managed security/ACL");

			logoutUser();
		} else {
			LOG.info("Not initializing anything for SHOGun2.");
		}
	}

	/**
	 * This method logs in the passed user. (The ACL system needs a user with
	 * ROLE_ADMIN to write ACL entries to the database).
	 * 
	 * @param user
	 */
	private void logInUser(User user) {
		Authentication authRequest = new UsernamePasswordAuthenticationToken(
				user.getAccountName(), user.getPassword());

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
				new ClassPathResource(aclCleanupScriptPath));

		try {
			rdp.populate(aclDataSource.getConnection());
			LOG.info("Cleaned up ACL tables...");
		} catch (ScriptException | SQLException e) {
			LOG.error("Could not clean up ACL tables: " + e.getMessage());
		}
	}
}
