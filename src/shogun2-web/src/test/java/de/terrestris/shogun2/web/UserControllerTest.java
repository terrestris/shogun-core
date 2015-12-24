package de.terrestris.shogun2.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.terrestris.shogun2.service.PasswordResetTokenService;

/**
 * @author Nils Bühner
 *
 */
public class UserControllerTest {

	private MockMvc mockMvc;

	@Mock
	private PasswordResetTokenService tokenService;

	@InjectMocks
	private UserController UserController;

	@Before
	public void setUp() {

		// Process mock annotations
		MockitoAnnotations.initMocks(this);

		// Setup Spring test in standalone mode
		this.mockMvc = MockMvcBuilders.standaloneSetup(UserController).build();
	}

	@Test
	public void resetPassword_shouldSendMailAsExpected() throws Exception {

		String email = "test@example.com";

		// mock service
		doNothing()
			.when(tokenService)
				.sendResetPasswordMail(
					any(HttpServletRequest.class),
					eq(email)
				);

		// Perform and test the POST-Request
		mockMvc.perform(post("/user/resetPassword.action").param("email", email))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.*", hasSize(3)))
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.total", is(1)))
			.andExpect(jsonPath("$.data", containsString("Password reset has been requested.")));

		verify(tokenService, times(1)).sendResetPasswordMail(any(HttpServletRequest.class), eq(email));
		verifyNoMoreInteractions(tokenService);
	}

	@Test
	public void resetPassword_shouldCatchExceptions() throws Exception {

		String email = "test@example.com";

		// mock service
		doThrow(new RuntimeException("errormsg"))
			.when(tokenService)
				.sendResetPasswordMail(
					any(HttpServletRequest.class),
					eq(email)
				);

		// Perform and test the POST-Request
		mockMvc.perform(post("/user/resetPassword.action").param("email", email))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.*", hasSize(2)))
			.andExpect(jsonPath("$.success", is(false)))
			.andExpect(jsonPath("$.message", is("An error has occured during passwort reset request.")));

		verify(tokenService, times(1)).sendResetPasswordMail(any(HttpServletRequest.class), eq(email));
		verifyNoMoreInteractions(tokenService);
	}

	@Test
	public void changePassword_shouldResetPasswordAsExpected() throws Exception {

		String password = "secret";
		String token = "token";

		// mock service
		doNothing().when(tokenService).changePassword(password, token);

		// Perform and test the POST-Request
		mockMvc.perform(post("/user/changePassword.action")
				.param("password", password)
				.param("token", token))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.*", hasSize(3)))
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.total", is(1)))
			.andExpect(jsonPath("$.data", is("Your password was changed successfully.")));

		verify(tokenService, times(1)).changePassword(password, token);
		verifyNoMoreInteractions(tokenService);
	}

	@Test
	public void changePassword_shouldCatchExceptions() throws Exception {

		String password = "secret";
		String token = "token";

		// mock service
		doThrow(new RuntimeException("errormsg")).when(tokenService).changePassword(password, token);

		// Perform and test the POST-Request
		mockMvc.perform(post("/user/changePassword.action")
				.param("password", password)
				.param("token", token))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.*", hasSize(2)))
			.andExpect(jsonPath("$.success", is(false)))
			.andExpect(jsonPath("$.message", containsString("Could not change the password.")));

		verify(tokenService, times(1)).changePassword(password, token);
		verifyNoMoreInteractions(tokenService);
	}
}
