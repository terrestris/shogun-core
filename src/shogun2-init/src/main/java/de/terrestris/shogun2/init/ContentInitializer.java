package de.terrestris.shogun2.init;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.model.User;
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

	@Autowired
	@Qualifier("aclDataSource")
	protected DataSource aclDataSource;

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

			giveAdminRoleToSystem();

			initService.addPermission(adminApp, new PrincipalSid("admin"),
					BasePermission.READ);
			initService.addPermission(userApp, new PrincipalSid("admin"),
					BasePermission.READ);
			initService.addPermission(userApp, new PrincipalSid("user"),
					BasePermission.READ);

			LOG.info("Managed security/ACL");

			SecurityContextHolder.getContext().setAuthentication(null);
		} else {
			LOG.info("Not initializing anything for SHOGun2.");
		}
	}

	/**
	 * This method assigns the ROLE_ADMIN to the system, which is needed to use
	 * the ACL service successfully.
	 */
	private void giveAdminRoleToSystem() {
		// Give ROLE_ADMIN to the system.
		Collection<SimpleGrantedAuthority> authorities = Collections
				.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));

		Authentication initAuth = new UsernamePasswordAuthenticationToken(
				"system", null, authorities);
		SecurityContextHolder.getContext().setAuthentication(initAuth);
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
