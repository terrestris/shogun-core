package de.terrestris.shogun2.security;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.terrestris.shogun2.model.Role;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.service.UserGroupService;
import de.terrestris.shogun2.service.UserService;

/**
 * @author Nils Bühner
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-encoder-bean.xml" })
public class Shogun2AuthenticationProviderTest {

	@Mock
	private UserService userService;

	@Mock
	private UserGroupService userGroupService;

	@Mock
	private RoleHierarchyImpl roleHierarchy;

	@InjectMocks
	private Shogun2AuthenticationProvider authProvider;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Before
	public void setUp() {
		// Process mock annotations
		MockitoAnnotations.initMocks(this);
		authProvider.setPasswordEncoder(passwordEncoder);
	}

	/**
	 * Tests whether the authenticate method authenticates a user correctly and
	 * assigns roles from the user object, but also from the userGroups the user
	 * is member of.
	 */
	@Test
	public void authenticate_shouldAuthenticateAndAssignRolesFromUserAndUserGroups() {

		// 1. Mock an authentication request object
		final String shogun2UserName = "user";
		final String shogun2UserPass = "password";
		final User userToAuth = createUserMock(shogun2UserName, shogun2UserPass);

		final Role adminRole = new Role("ROLE_ADMIN");
		final Role userRole = new Role("ROLE_USER");

		// grant admin role to the user
		userToAuth.getRoles().add(adminRole);

		Authentication authRequest = mock(Authentication.class);
		when(authRequest.getName()).thenReturn(shogun2UserName);
		when(authRequest.getCredentials()).thenReturn(shogun2UserPass);

		// 2. Mock the userService
		when(userService.findByAccountName(shogun2UserName)).thenReturn(userToAuth);

		// 3. Mock the userGroupService
		doAnswer(new Answer<Set<UserGroup>>() {

			public Set<UserGroup> answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException {

				Set<UserGroup> userGroups = new HashSet<UserGroup>();

				UserGroup defaultUserGroup = new UserGroup();

				// add ROLE_USER to the group
				defaultUserGroup.getRoles().add(userRole);

				userGroups.add(defaultUserGroup);

				return userGroups;
			}
		}).when(userGroupService).findGroupsOfUser(userToAuth);

		// 4. Mock the roleHierarchy (return empty collection)
		when(
			roleHierarchy
					.getReachableGrantedAuthorities(anyCollectionOf(GrantedAuthority.class)))
			.thenReturn(new HashSet<GrantedAuthority>());

		// 5. Call the authenticate method with the mocked object
		Authentication authResult = authProvider.authenticate(authRequest);

		// 6. Assert that the authResult is valid
		assertNotNull(authResult);
		assertThat(authResult, instanceOf(UsernamePasswordAuthenticationToken.class));
		assertTrue(authResult.isAuthenticated());

		assertThat(authResult.getPrincipal(), instanceOf(User.class));
		assertEquals(userToAuth, authResult.getPrincipal());
		assertTrue(passwordEncoder.matches(shogun2UserPass, authResult.getCredentials().toString()));

		// assert that the user now has the ROLE_ADMIN (from user object) and
		// ROLE_USER (from the default user group)
		GrantedAuthority adminAuthority = new SimpleGrantedAuthority(adminRole.getName());
		GrantedAuthority userAuthority = new SimpleGrantedAuthority(userRole.getName());

		// thx to http://stackoverflow.com/a/12167781
		assertThat(authResult.getAuthorities(),
				IsIterableContainingInAnyOrder
						.<GrantedAuthority> containsInAnyOrder(adminAuthority, userAuthority));
	}

	/**
	 * Tests whether a {@link UsernameNotFoundException} is thrown when a user
	 * could not be found.
	 */
	@Test(expected=UsernameNotFoundException.class)
	public void authenticate_shouldThrowUsernameNotFoundExceptionIfUserNotFound() {

		// 1. Mock an authentication request object
		final String shogun2UserName = "user";

		// 2. Mock the auth request
		Authentication authRequest = mock(Authentication.class);
		when(authRequest.getName()).thenReturn(shogun2UserName);

		// 3. Mock the userService by returning null (-> no user found)
		when(userService.findByAccountName(shogun2UserName)).thenReturn(null);

		// 4. Call the authenticate method with the mocked object to provoke
		// the expected UserNameNotFoundException
		authProvider.authenticate(authRequest);
	}

	/**
	 * Tests whether a {@link BadCredentialsException} is thrown when the
	 * password does not match.
	 */
	@Test(expected=BadCredentialsException.class)
	public void authenticate_shouldThrowBadCredentialsExceptionIfPasswordDoesNotMatch() {

		// 1. Mock an authentication request object
		final String shogun2UserName = "user";
		final String correctPassword = "correctPassword";
		final User userToAuth = createUserMock(shogun2UserName, correctPassword);

		final String wrongPassword = "wrongPassword";

		// 2. Mock the auth request with the wrong password
		Authentication authRequest = mock(Authentication.class);
		when(authRequest.getName()).thenReturn(shogun2UserName);
		when(authRequest.getCredentials()).thenReturn(wrongPassword);

		// 3. Mock the userService
		when(userService.findByAccountName(shogun2UserName)).thenReturn(userToAuth);

		// 4. Call the authenticate method with the mocked object to provoke
		// the expected BadCredentialsException
		authProvider.authenticate(authRequest);
	}

	/**
	 * Creates a simple user mock with an encrypted password.
	 *
	 * @param accountName
	 * @param password
	 * @return
	 */
	private User createUserMock(final String accountName, final String password) {
		final String encryptedPassword = passwordEncoder.encode(password);
		final User userToAuth = new User("firstName", "lastName", accountName, encryptedPassword);
		return userToAuth;
	}

}
