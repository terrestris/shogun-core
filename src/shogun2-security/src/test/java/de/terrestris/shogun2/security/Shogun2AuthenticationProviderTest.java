/**
 *
 */
package de.terrestris.shogun2.security;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hibernate.criterion.SimpleExpression;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import de.terrestris.shogun2.dao.UserDao;
import de.terrestris.shogun2.model.User;

/**
 * @author Nils Bühner
 *
 */
public class Shogun2AuthenticationProviderTest {

	@Mock
	private UserDao userDao;

	@InjectMocks
	private Shogun2AuthenticationProvider authProvider;

	@Before
	public void setUp() {
		// Process mock annotations
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void authenticate_shouldAssignRoleUser() {

		// 1. Mock an authentication request object
		final String shogun2UserName = "user";
		final String shogun2UserPass = "user";

		Authentication authRequest = mock(Authentication.class);
		when(authRequest.getName()).thenReturn(shogun2UserName);
		when(authRequest.getCredentials()).thenReturn(shogun2UserPass);

		// 2. Mock the userDao
		doAnswer(new Answer<List<User>>() {

			public List<User> answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException {

				User u = new User("firstName", "lastName", shogun2UserName, shogun2UserPass);

				return Collections.singletonList(u);
			}
		}).when(userDao).findByCriteria(any(SimpleExpression.class));

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

		assertThat(authResult.getPrincipal(), instanceOf(String.class));
		assertEquals(shogun2UserName, authResult.getPrincipal());
		assertEquals(shogun2UserPass, authResult.getCredentials());

		// thx to http://stackoverflow.com/a/12167781
		assertThat(authResult.getAuthorities(),
				IsIterableContainingInAnyOrder
						.<GrantedAuthority> containsInAnyOrder(expectedRole));
	}

}
