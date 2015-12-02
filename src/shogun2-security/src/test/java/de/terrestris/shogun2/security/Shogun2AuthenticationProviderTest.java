/**
 *
 */
package de.terrestris.shogun2.security;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.terrestris.shogun2.model.User;
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

	@Test
	public void authenticate_shouldAssignRoleUser() {

		// 1. Mock an authentication request object
		final String shogun2UserName = "user";
		final String shogun2UserPass = "user";
		final String encryptedPassword = passwordEncoder.encode(shogun2UserPass);
		final User userToAuth = new User("firstName", "lastName", shogun2UserName, encryptedPassword);

		Authentication authRequest = mock(Authentication.class);
		when(authRequest.getName()).thenReturn(shogun2UserName);
		when(authRequest.getCredentials()).thenReturn(shogun2UserPass);

		// 2. Mock the userDao
		doAnswer(new Answer<User>() {

			public User answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException {

				return userToAuth;
			}
		}).when(userService).findByAccountName(shogun2UserName);

		// 2. Call the authenticate method with the mocked object
//		Shogun2AuthenticationProvider authProvider = new Shogun2AuthenticationProvider();
		Authentication authResult = authProvider.authenticate(authRequest);

		// 3. Assert that the authResult is valid (e.g. that ROLE_USER has been
		// assigned)
		String expectedRoleName = "ROLE_USER";
		GrantedAuthority expectedRole = new SimpleGrantedAuthority(
				expectedRoleName);

		assertNotNull(authResult);
		assertThat(authResult,
				instanceOf(UsernamePasswordAuthenticationToken.class));
		assertTrue(authResult.isAuthenticated());

		assertThat(authResult.getPrincipal(), instanceOf(User.class));
		assertEquals(userToAuth, authResult.getPrincipal());
		assertTrue(passwordEncoder.matches(shogun2UserPass, authResult.getCredentials().toString()));

		// thx to http://stackoverflow.com/a/12167781
		assertThat(authResult.getAuthorities(),
				IsIterableContainingInAnyOrder
						.<GrantedAuthority> containsInAnyOrder(expectedRole));
	}

}
