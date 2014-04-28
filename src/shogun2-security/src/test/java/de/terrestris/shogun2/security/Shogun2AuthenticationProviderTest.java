/**
 *
 */
package de.terrestris.shogun2.security;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Nils Bühner
 *
 */
public class Shogun2AuthenticationProviderTest {

	@Test
	public void authenticate_shouldAssignRoleUser() {

		// 1. Mock an authentication request object
		String shogun2UserName = "Some SHOGun2 User";
		String shogun2UserPass = "SomePassword";

		Authentication authRequest = mock(Authentication.class);
		when(authRequest.getName()).thenReturn(shogun2UserName);
		when(authRequest.getCredentials()).thenReturn(shogun2UserPass);

		// 2. Call the authenticate method with the mocked object
		Shogun2AuthenticationProvider authProvider = new Shogun2AuthenticationProvider();
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

		assertThat(authResult.getPrincipal(), instanceOf(UserDetails.class));
		assertEquals(shogun2UserName, ((UserDetails) authResult.getPrincipal()).getUsername());
		assertEquals(shogun2UserPass, authResult.getCredentials());

		// thx to http://stackoverflow.com/a/12167781
		assertThat(authResult.getAuthorities(),
				IsIterableContainingInAnyOrder
						.<GrantedAuthority> containsInAnyOrder(expectedRole));
	}

}
