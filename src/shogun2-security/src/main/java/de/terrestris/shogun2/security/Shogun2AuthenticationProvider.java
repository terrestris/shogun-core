package de.terrestris.shogun2.security;
/**
 *
 */


import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Nils Buehner
 *
 */
public class Shogun2AuthenticationProvider implements AuthenticationProvider {

	/**
	 * The Logger
	 */
	private static Logger log = Logger
			.getLogger(Shogun2AuthenticationProvider.class);

	/**
	 *
	 * @see org.springframework.security.authentication.AuthenticationProvider#
	 *      authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		log.info("Authenticating a SHOGun2-User");

		// get username/password
		String userName = authentication.getName();
		String password = (String) authentication.getCredentials();

		// build granted authorities
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
				"ROLE_USER");
		grantedAuthorities.add(grantedAuthority);

		// create logged in user object
		UserDetails userInformation = new User(userName, password, true, true,
				true, true, grantedAuthorities);

		// create corresponding token to forward in Spring Security's filter
		// chain
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
				userInformation, authentication.getCredentials(),
				grantedAuthorities);

		log.info("Finished authentication of the SHOGun2-User");

		return authenticationToken;
	}

	/**
	 *
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports
	 *      (java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class
				.isAssignableFrom(authentication));
	}
}
