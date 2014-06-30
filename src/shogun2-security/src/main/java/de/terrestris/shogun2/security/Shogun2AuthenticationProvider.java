package de.terrestris.shogun2.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.UserDao;
import de.terrestris.shogun2.model.User;

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
	private UserDao userDao;

	/**
	 * 
	 * @see org.springframework.security.authentication.AuthenticationProvider#
	 *      authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	@Transactional
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		// get username/password
		String accountName = authentication.getName();
		String password = (String) authentication.getCredentials();

		LOG.debug("Trying to authenticate SHOGun2-User '" + accountName + "'");

		// the account name is unique, so we can find at most one user
		SimpleExpression eqAccountName = Restrictions.eq("accountName",
				accountName);

		List<User> userList = userDao.findByCriteria(eqAccountName);
		User shogun2User = null;

		// prepare authorities
		GrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
		GrantedAuthority adminAuthority = new SimpleGrantedAuthority(
				"ROLE_ADMIN");
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();

		if (userList == null || userList.size() == 0) {
			throw new AuthenticationCredentialsNotFoundException(
					"Could not find user " + accountName);
		} else {
			shogun2User = userList.get(0);

			// TODO check password
			// TODO get authority from user object, i.e. db

			if (accountName.equals("admin")) {
				grantedAuthorities.add(adminAuthority);
			} else if (accountName.equals("user")) {
				grantedAuthorities.add(userAuthority);
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
			authResult = new UsernamePasswordAuthenticationToken(shogun2User,
					shogun2User.getPassword());
		} else {
			// if we pass some grantedAuthorities, isAuthenticated() of
			// authenticationToken will return true afterwards
			authResult = new UsernamePasswordAuthenticationToken(shogun2User,
					shogun2User.getPassword(), grantedAuthorities);
		}

		LOG.debug("SHOGun2-User '" + accountName + "' is authenticated: "
				+ authResult.isAuthenticated());

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
}
