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

		LOG.info("Authenticating a SHOGun2-User");

		// get username/password
		String userName = authentication.getName();
		String password = (String) authentication.getCredentials();

		// the account name is unique, so we can find at most one user
		SimpleExpression eqAccountName = Restrictions.eq("accountName",
				userName);

		List<User> userList = userDao.findByCriteria(eqAccountName);
		User shogun2User = null;

		// prepare authorities
		GrantedAuthority userAuthority = new SimpleGrantedAuthority(
				"ROLE_USER");
		GrantedAuthority adminAuthority = new SimpleGrantedAuthority(
				"ROLE_ADMIN");
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();

		if (userList == null || userList.size() == 0) {
			throw new AuthenticationCredentialsNotFoundException("Could not find user " + userName);
		} else {
			shogun2User = userList.get(0);

			// TODO check password
			// TODO get authority from user object, i.e. db

			if(userName.equals("admin")){
				grantedAuthorities.add(adminAuthority);
			} else if (userName.equals("user")){
				grantedAuthorities.add(userAuthority);
			} else {
				throw new AuthenticationCredentialsNotFoundException("Could not detect authority for user " + userName);
			}
		}

		// create corresponding token to forward in Spring Security's filter
		// chain
		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
				shogun2User.getAccountName(), shogun2User.getPassword(),
				grantedAuthorities);

		LOG.info("Finished authentication of the SHOGun2-User");

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
