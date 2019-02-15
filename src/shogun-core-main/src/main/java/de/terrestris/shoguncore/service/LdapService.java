package de.terrestris.shoguncore.service;

import org.apache.logging.log4j.Logger;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * terrestris GmbH & Co. KG
 * <p>
 * Ldap service
 */
@Service
public class LdapService {

    private static final Logger LOGGER = getLogger(LdapService.class);

    private LdapTemplate ldapTemplate;

    /**
     * Set the ldap template property for ldap access.
     *
     * @param ldapTemplate the template to set
     */
    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    /**
     * Authenticate against ldap.
     *
     * @param username the username
     * @param password the password
     */
    public void authenticate(String username, String password) {
        ldapTemplate.authenticate(query().where("objectClass").is("simpleSecurityObject").and("cn").is(username), password);
        LOGGER.info("Successfully authenticated " + username);
    }

    /**
     * Extract groups from ldap.
     *
     * @param username username to search for
     * @param property the property to extract the groups from
     * @return a list of group names from ldap
     */
    public List<String> getGroups(String username, String property) {
        final List<String> result = new ArrayList<>();
        ldapTemplate.search(query().where("cn").is(username), new AttributesMapper<String>() {
            public String mapFromAttributes(Attributes attrs) throws NamingException {
                NamingEnumeration<?> ous = attrs.get(property).getAll();
                // since we can generate multiple values here but may only return a single string, we ignore
                // the ldapTemplate#search result and just put the values in our own list, returning an empty string
                // which is effectively ignored
                while (ous.hasMore()) {
                    result.add((String) ous.next());
                }
                return "";
            }
        });
        return result;
    }

}
