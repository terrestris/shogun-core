package de.terrestris.shoguncore.security;

import de.terrestris.shoguncore.dao.UserDao;
import de.terrestris.shoguncore.dao.UserGroupDao;
import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.UserGroup;
import de.terrestris.shoguncore.service.UserGroupService;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Nils Bühner
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/test-encoder-bean.xml"})
public class ShogunCoreAuthenticationProviderTest {

    @Mock
    private UserDao<User> userDao;

    @Mock
    private UserGroupService<UserGroup, UserGroupDao<UserGroup>> userGroupService;

    @Mock
    private RoleHierarchyImpl roleHierarchy;

    @InjectMocks
    private ShogunCoreAuthenticationProvider authProvider;

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
        final String shogunUserName = "user";
        final String shogunUserPass = "password";
        final User userToAuth = createUserMock(shogunUserName, shogunUserPass);

        final Role adminRole = new Role("ROLE_ADMIN");
        final Role userRole = new Role("ROLE_USER");

        final UserGroup userGroup = new UserGroup();

        // set user as active
        userToAuth.setActive(true);

        // grant admin role to the user
        userToAuth.getRoles().add(adminRole);

        // add role to group and groups to user
        Set<UserGroup> userGroups = new HashSet<UserGroup>();
        userGroup.getRoles().add(userRole);
        userGroups.add(userGroup);
        userToAuth.setUserGroups(userGroups);

        Authentication authRequest = mock(Authentication.class);
        when(authRequest.getName()).thenReturn(shogunUserName);
        when(authRequest.getCredentials()).thenReturn(shogunUserPass);

        // 2. Mock the userDao
        when(userDao.findByAccountName(shogunUserName)).thenReturn(userToAuth);

        // 3. Mock the roleHierarchy (return empty collection)
        when(
            roleHierarchy
                .getReachableGrantedAuthorities(anyCollectionOf(GrantedAuthority.class)))
            .thenReturn(new HashSet<GrantedAuthority>());

        // 4. Call the authenticate method with the mocked object
        Authentication authResult = authProvider.authenticate(authRequest);

        // 5. Assert that the authResult is valid
        assertNotNull(authResult);
        assertThat(authResult, instanceOf(UsernamePasswordAuthenticationToken.class));
        assertTrue(authResult.isAuthenticated());

        assertThat(authResult.getPrincipal(), instanceOf(User.class));
        assertEquals(userToAuth, authResult.getPrincipal());
        assertTrue(passwordEncoder.matches(shogunUserPass, authResult.getCredentials().toString()));

        // assert that the user now has the ROLE_ADMIN (from user object) and
        // ROLE_USER (from the default user group)
        GrantedAuthority adminAuthority = new SimpleGrantedAuthority(adminRole.getName());
        GrantedAuthority userAuthority = new SimpleGrantedAuthority(userRole.getName());

        // thx to http://stackoverflow.com/a/12167781
        assertThat(authResult.getAuthorities(),
            IsIterableContainingInAnyOrder
                .<GrantedAuthority>containsInAnyOrder(adminAuthority, userAuthority));
    }

    @Test
    public void authenticate_shouldNotAuthenticateIfNoAuthorities() {
        // 1. Mock an authentication request object
        final String shogunUserName = "user";
        final String shogunUserPass = "password";
        final User userToAuth = createUserMock(shogunUserName, shogunUserPass);

        // set user as active
        userToAuth.setActive(true);

        Authentication authRequest = mock(Authentication.class);
        when(authRequest.getName()).thenReturn(shogunUserName);
        when(authRequest.getCredentials()).thenReturn(shogunUserPass);

        // 2. Mock the userDao
        when(userDao.findByAccountName(shogunUserName)).thenReturn(userToAuth);

        // 3. Mock the roleHierarchy (return empty collection)
        when(
                roleHierarchy
                        .getReachableGrantedAuthorities(anyCollectionOf(GrantedAuthority.class)))
                .thenReturn(new HashSet<GrantedAuthority>());

        // 4. Call the authenticate method with the mocked object
        Authentication authResult = authProvider.authenticate(authRequest);

        // 5. Assert that the authResult is valid
        assertNotNull(authResult);
        assertThat(authResult, instanceOf(UsernamePasswordAuthenticationToken.class));
        assertFalse(authResult.isAuthenticated());
    }

    /**
     * Tests whether a {@link UsernameNotFoundException} is thrown when a user
     * could not be found.
     */
    @Test(expected = UsernameNotFoundException.class)
    public void authenticate_shouldThrowUsernameNotFoundExceptionIfUserNotFound() {

        // 1. Mock an authentication request object
        final String shogunUserName = "user";

        // 2. Mock the auth request
        Authentication authRequest = mock(Authentication.class);
        when(authRequest.getName()).thenReturn(shogunUserName);

        // 3. Mock the userDao by returning null (-> no user found)
        when(userDao.findByAccountName(shogunUserName)).thenReturn(null);

        // 4. Call the authenticate method with the mocked object to provoke
        // the expected UserNameNotFoundException
        authProvider.authenticate(authRequest);
    }

    /**
     * Tests whether a {@link BadCredentialsException} is thrown when the
     * password does not match.
     */
    @Test(expected = BadCredentialsException.class)
    public void authenticate_shouldThrowBadCredentialsExceptionIfPasswordDoesNotMatch() {

        // 1. Mock an authentication request object
        final String shogunUserName = "user";
        final String correctPassword = "correctPassword";
        final User userToAuth = createUserMock(shogunUserName, correctPassword);

        final String wrongPassword = "wrongPassword";

        // set user as active
        userToAuth.setActive(true);

        // 2. Mock the auth request with the wrong password
        Authentication authRequest = mock(Authentication.class);
        when(authRequest.getName()).thenReturn(shogunUserName);
        when(authRequest.getCredentials()).thenReturn(wrongPassword);

        // 3. Mock the userDao
        when(userDao.findByAccountName(shogunUserName)).thenReturn(userToAuth);

        // 4. Call the authenticate method with the mocked object to provoke
        // the expected BadCredentialsException
        authProvider.authenticate(authRequest);
    }

    /**
     * Tests whether a {@link DisabledException} is thrown when the user is not
     * active.
     */
    @Test(expected = DisabledException.class)
    public void authenticate_shouldThrowDisabledExceptionIfUserIsInactive() {

        // 1. Mock an authentication request object
        final String shogunUserName = "user";
        final String correctPassword = "correctPassword";
        final User userToAuth = createUserMock(shogunUserName, correctPassword);

        // set user as inactive
        userToAuth.setActive(false);

        // 2. Mock the auth request for the inactive user
        Authentication authRequest = mock(Authentication.class);
        when(authRequest.getName()).thenReturn(shogunUserName);
        when(authRequest.getCredentials()).thenReturn(correctPassword);

        // 3. Mock the userDao
        when(userDao.findByAccountName(shogunUserName)).thenReturn(userToAuth);

        // 4. Call the authenticate method with the mocked object to provoke
        // the expected DisabledException
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

    @Test
    public void authentication_supportsUsernamePassword() {
        Assert.assertTrue("Authentication supports UsernamePasswordAuthenticationToken", authProvider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void hasPasswordEncoderSet() {
        assertNotNull(authProvider.getPasswordEncoder());
    }

}
