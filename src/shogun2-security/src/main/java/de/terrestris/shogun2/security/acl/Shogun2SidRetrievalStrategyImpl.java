package de.terrestris.shogun2.security.acl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.access.hierarchicalroles.NullRoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.domain.SidRetrievalStrategyImpl;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.SidRetrievalStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import de.terrestris.shogun2.model.User;

/**
 * This class can be used with Spring ACL as the strategy to build a SID from
 * the current authentication object, where the principal is a SHOGun2-User
 * object.
 * 
 * If the principal (of the current authentication) is NOT a SHOGun2 user
 * object, this class behaves like the default implementation
 * {@link SidRetrievalStrategyImpl}.
 * 
 * @author Nils Bühner
 * 
 */
public class Shogun2SidRetrievalStrategyImpl implements SidRetrievalStrategy {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger
			.getLogger(Shogun2SidRetrievalStrategyImpl.class);

	/**
	 * RoleHierarchy that may be passed in.
	 */
	private RoleHierarchy roleHierarchy = new NullRoleHierarchy();

	/**
	 * Default constructor
	 */
	public Shogun2SidRetrievalStrategyImpl() {
	}

	public Shogun2SidRetrievalStrategyImpl(RoleHierarchy roleHierarchy) {
		Assert.notNull(roleHierarchy, "RoleHierarchy must not be null");
		this.roleHierarchy = roleHierarchy;
	}

	@Override
	public List<Sid> getSids(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = roleHierarchy
				.getReachableGrantedAuthorities(authentication.getAuthorities());
		List<Sid> sids = new ArrayList<Sid>(authorities.size() + 1);

		Object principal = authentication.getPrincipal();
		// check if the principal is a SHOGun2 user
		if (principal instanceof User) {
			User shogun2User = (User) principal;
			String accountName = shogun2User.getAccountName();
			LOG.trace("Using shogun2-user's accountName to build SID: "
					+ accountName);
			sids.add(new PrincipalSid(accountName));
		} else {
			// fallback to default behaviour of the default implementation
			// (SidRetrievalStrategyImpl)
			sids.add(new PrincipalSid(authentication));
		}

		for (GrantedAuthority authority : authorities) {
			sids.add(new GrantedAuthoritySid(authority));
		}

		return sids;
	}

}
