package de.terrestris.shoguncore.web;

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

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.terrestris.shoguncore.dao.PasswordResetTokenDao;
import de.terrestris.shoguncore.dao.UserDao;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.token.PasswordResetToken;
import de.terrestris.shoguncore.service.PasswordResetTokenService;
import de.terrestris.shoguncore.service.UserService;

/**
 * @author Nils Bühner
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/test-encoder-bean.xml"})
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock(name = "passwordResetTokenService")
    private PasswordResetTokenService<PasswordResetToken, PasswordResetTokenDao<PasswordResetToken>> tokenService;

    @Mock(name = "service")
    private UserService<User, UserDao<User>> userService;

    /**
     * The controller to test
     */
    private UserController<User, UserDao<User>, UserService<User, UserDao<User>>> userController;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // init the controller to test. this is necessary as InjectMocks
        // annotation will not work with the constructors of the controllers
        // (entityClass). see https://goo.gl/jLbMZe
        userController = new UserController<User, UserDao<User>, UserService<User, UserDao<User>>>();
        userController.setService(userService);
        userController.setPasswordResetTokenService(tokenService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void registerUser_shouldWorkAsExpected() throws Exception {

        String email = "test@example.com";
        String rawPassword = "p@sSw0rd";
        boolean isActive = false;

        // mock result
        User registeredUser = new User();
        registeredUser.setEmail(email);
        registeredUser.setAccountName(email);
        registeredUser.setPassword(passwordEncoder.encode(rawPassword));
        registeredUser.setActive(isActive);

        // mock service
        when(userService.registerUser(any(User.class), any(HttpServletRequest.class)))
            .thenReturn(registeredUser);


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

        verify(userService, times(1)).registerUser(any(User.class), any(HttpServletRequest.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void registerUser_shouldCatchExceptions() throws Exception {

        String email = "test@example.com";
        String rawPassword = "p@sSw0rd";

        // mock service
        doThrow(new Exception("errormsg"))
            .when(userService).registerUser(any(User.class), any(HttpServletRequest.class));


        // Perform and test the POST-Request
        mockMvc.perform(post("/user/register.action")
            .param("email", email)
            .param("password", rawPassword))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(jsonPath("$.*", hasSize(2)))
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", is("Could not register a new user.")));

        verify(userService, times(1)).registerUser(any(User.class), any(HttpServletRequest.class));
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
        final String exceptionMsg = "errormsg";
        doThrow(new RuntimeException(exceptionMsg))
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
            .andExpect(jsonPath("$.message", is(exceptionMsg)));

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
