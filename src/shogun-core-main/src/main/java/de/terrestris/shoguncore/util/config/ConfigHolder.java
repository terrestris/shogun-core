package de.terrestris.shoguncore.util.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Helper component class to make property values available for use in
 * annotations. Credits go to http://stackoverflow.com/a/18207602
 *
 * @author Nils Bühner
 */
@Component("configHolder")
public class ConfigHolder {

    /**
     * The name of the (super) admin role. If the property configured in the
     * {@link Value} annotation is not present, the empty string "" will be used
     * as a fallback.
     */
    @Value("${role.superAdminRoleName:}")
    private String superAdminRoleName;

    /**
     * The name of the default user role. If the property configured in the
     * {@link Value} annotation is not present, the empty string "" will be used
     * as a fallback.
     */
    @Value("${role.defaultUserRoleName:}")
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
