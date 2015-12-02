package de.terrestris.shogun2.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.service.UserService;

/**
 * @author Nils Bühner
 *
 */
public class Shogun2AuthenticationProvider implements AuthenticationProvider {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger
			.getLogger(Shogun2AuthenticationProvider.class);

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 *
	 * @see org.springframework.security.authentication.AuthenticationProvider#
	 *      authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		// prepare an exception
		final String exceptionMessage = "User and password do not match.";

		// get username/password
		String accountName = authentication.getName();
		String rawPassword = (String) authentication.getCredentials();

		LOG.debug("Trying to authenticate User '" + accountName + "'");

		User user = userService.findByAccountName(accountName);

		// prepare authorities
		GrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
		GrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();

		String encryptedPassword = null;

		if (user == null) {
			LOG.warn("No user for account name '" + accountName + "' could be found.");
			throw new UsernameNotFoundException(exceptionMessage);
		} else {

			encryptedPassword = user.getPassword();

			// check if rawPassword matches the hash from db
			if(passwordEncoder.matches(rawPassword, encryptedPassword)) {

				// TODO: become smarter here by getting authority from
				// user object/database here
				if (accountName.equals("admin")) {
					// TODO !
					grantedAuthorities.add(adminAuthority);
				} else {
					// TODO !
					grantedAuthorities.add(userAuthority);
				}
			} else {
				LOG.warn("The given password for user '" + accountName + "' does not match.");
				throw new BadCredentialsException(exceptionMessage);
			}

		}

		// Create corresponding token to forward in Spring Security's filter
		// chain. We will use the SHOGun2 user as the principal.
		Authentication authResult = null;
		if (grantedAuthorities.isEmpty()) {
			// if the user has no authorities, we will build the
			// UsernamePasswordAuthenticationToken without authorities, which
			// leads to an unauthenticated user, i.e. isAuthenticated() of
			// authenticationToken will return false afterwards.
			LOG.warn("The user '" + accountName + "' has no authorities and will thereby NOT be authenticated.");
			authResult = new UsernamePasswordAuthenticationToken(user, encryptedPassword);
		} else {
			// if we pass some grantedAuthorities, isAuthenticated() of
			// authenticationToken will return true afterwards
			authResult = new UsernamePasswordAuthenticationToken(user, encryptedPassword, grantedAuthorities);
		}

		final boolean isAuthenticated = authResult.isAuthenticated();
		final String authLog = isAuthenticated ? "could succesfully" : "could NOT";
		LOG.debug("The User '" + accountName + "' " + authLog  + " be authenticated.");

		return authResult;
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

	/**
	 * @return the passwordEncoder
	 */
	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	/**
	 * @param passwordEncoder the passwordEncoder to set
	 */
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
}
