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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.service.PasswordResetTokenService;
import de.terrestris.shogun2.service.UserService;

/**
 * @author Nils Bühner
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-encoder-bean.xml" })
public class UserControllerTest {

	private MockMvc mockMvc;

	@Mock
	private PasswordResetTokenService tokenService;

	@Mock
	private UserService userService;

	@InjectMocks
	private UserController UserController;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Before
	public void setUp() {

		// Process mock annotations
		MockitoAnnotations.initMocks(this);

		// Setup Spring test in standalone mode
		this.mockMvc = MockMvcBuilders.standaloneSetup(UserController).build();
	}

	@Test
	public void registerUser_shouldWorkAsExpected() throws Exception {

		String email = "test@example.com";
		String rawPassword = "p@sSw0rd";
		boolean isActive = false;

		User user = new User();
		user.setEmail(email);
		user.setAccountName(email);
		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setActive(isActive);

		// mock service
		when(userService.registerUser(
				eq(email), eq(rawPassword), eq(isActive), any(HttpServletRequest.class)))
			.thenReturn(user);


		// Perform and test the POST-Request
		mockMvc.perform(post("/user/register.action")
				.param("email", email)
				.param("password", rawPassword))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.*", hasSize(3)))
			.andExpect(jsonPath("$.success", is(true)))
			.andExpect(jsonPath("$.total", is(1)))
			.andExpect(jsonPath("$.data", containsString("You have been registered.")))
			.andExpect(jsonPath("$.data", containsString(email)));

		verify(userService, times(1)).registerUser(
				eq(email), eq(rawPassword), eq(isActive), any(HttpServletRequest.class));
		verifyNoMoreInteractions(userService);
	}

	@Test
	public void registerUser_shouldCatchExceptions() throws Exception {

		String email = "test@example.com";
		String rawPassword = "p@sSw0rd";
		boolean isActive = false;

		// mock service
		doThrow(new Exception("errormsg"))
			.when(userService).registerUser(
				eq(email), eq(rawPassword), eq(isActive), any(HttpServletRequest.class));


		// Perform and test the POST-Request
		mockMvc.perform(post("/user/register.action")
				.param("email", email)
				.param("password", rawPassword))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.*", hasSize(2)))
			.andExpect(jsonPath("$.success", is(false)))
			.andExpect(jsonPath("$.message", is("Could not register a new user.")));

		verify(userService, times(1)).registerUser(
				eq(email), eq(rawPassword), eq(isActive), any(HttpServletRequest.class));
		verifyNoMoreInteractions(userService);
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
		doNothing().when(tokenService).validateTokenAndUpdatePassword(password, token);

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

		verify(tokenService, times(1)).validateTokenAndUpdatePassword(password, token);
		verifyNoMoreInteractions(tokenService);
	}

	@Test
	public void changePassword_shouldCatchExceptions() throws Exception {

		String password = "secret";
		String token = "token";

		// mock service
		doThrow(new RuntimeException("errormsg")).when(tokenService).validateTokenAndUpdatePassword(password, token);

		// Perform and test the POST-Request
		mockMvc.perform(post("/user/changePassword.action")
				.param("password", password)
				.param("token", token))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.*", hasSize(2)))
			.andExpect(jsonPath("$.success", is(false)))
			.andExpect(jsonPath("$.message", containsString("Could not change the password.")));

		verify(tokenService, times(1)).validateTokenAndUpdatePassword(password, token);
		verifyNoMoreInteractions(tokenService);
	}
}
