package de.terrestris.shogun2.util.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Helper component class to make property values available for use in
 * annotations. Credits go to http://stackoverflow.com/a/18207602
 *
 * @author Nils Bühner
 *
 */
@Component("configHolder")
public class ConfigHolder {

	@Value("${role.superAdminRoleName}")
	private String superAdminRoleName;

	@Value("${role.defaultUserRoleName}")
	private String defaultUserRoleName;

	/**
	 * @return the superAdminRoleName
	 */
	public String getSuperAdminRoleName() {
		return superAdminRoleName;
	}

	/**
	 * @return the defaultUserRoleName
	 */
	public String getDefaultUserRoleName() {
		return defaultUserRoleName;
	}

}
