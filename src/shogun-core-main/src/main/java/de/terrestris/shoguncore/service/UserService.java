package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.RegistrationTokenDao;
import de.terrestris.shoguncore.dao.RoleDao;
import de.terrestris.shoguncore.dao.UserDao;
import de.terrestris.shoguncore.model.Role;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.UserGroup;
import de.terrestris.shoguncore.model.token.RegistrationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Service class for the {@link User} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("userService")
public class UserService<E extends User, D extends UserDao<E>> extends
    PersonService<E, D> {

    /**
     * Registration token service
     */
    @Autowired
    protected RegistrationTokenService<RegistrationToken, RegistrationTokenDao<RegistrationToken>> registrationTokenService;
    /**
     * Role service
     */
    @Autowired
    protected RoleService<Role, RoleDao<Role>> roleService;
    /**
     * The autowired PasswordEncoder
     */
    @Autowired
    protected PasswordEncoder passwordEncoder;
    /**
     * The default user role that is assigned to a user if he activates his
     * account. ATTENTION: This autowired bean will NOT have an ID if the
     * system did not boot with hibernate/CREATE mode and SHOGun-Core content
     * initialization!
     * <p>
     * TODO: We should only autowire a string with the role name...
     */
    @Autowired(required = false)
    @Qualifier("userRole")
    private Role defaultUserRole;

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public UserService() {
        this((Class<E>) User.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected UserService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("userDao")
    public void setDao(D dao) {
        super.setDao(dao);
    }

    /**
     * Returns the user for the given (unique) account name.
     * If no user was found, null will be returned.
     *
     * @param accountName A unique account name.
     * @return The unique user for the account name or null.
     */
    @PostAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#accountName, 'READ')")
    @Transactional(readOnly = true)
    public E findByAccountName(String accountName) {
        return dao.findByAccountName(accountName);
    }

    /**
     * @param email
     * @return
     */
    @PostAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#email, 'READ')")
    @Transactional(readOnly = true)
    public E findByEmail(String email) {
        return dao.findByEmail(email);
    }

    /**
     * Registers a new user. Initially, the user will be inactive. An email with
     * an activation link will be sent to the user.
     *
     * @param user    A user with an UNencrypted password (!)
     * @param request
     * @throws Exception
     */
    public E registerUser(E user, HttpServletRequest request) throws Exception {

        String email = user.getEmail();

        // check if a user with the email already exists
        E existingUser = dao.findByEmail(email);

        if (existingUser != null) {
            final String errorMessage = "User with eMail '" + email + "' already exists.";
            logger.info(errorMessage);
            throw new Exception(errorMessage);
        }

        user = this.persistNewUser(user, true);

        // create a token for the user and send an email with an "activation" link
        registrationTokenService.sendRegistrationActivationMail(request, user);

        return user;
    }

    /**
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void activateUser(String tokenValue) throws Exception {

        RegistrationToken token = registrationTokenService.findByTokenValue(tokenValue);

        logger.debug("Trying to activate user account with token: " + tokenValue);

        // throws Exception if token is not valid
        registrationTokenService.validateToken(token);

        // set active=true
        E user = (E) token.getUser();
        user.setActive(true);

        // set the persisted default user role if available
        if (defaultUserRole != null) {
            final String defaultRoleName = defaultUserRole.getName();
            Role persistedDefaultUserRole = roleService.findByRoleName(defaultRoleName);

            if (persistedDefaultUserRole != null) {
                // assign the default user role
                user.getRoles().add(persistedDefaultUserRole);
            }
        }

        // update the user
        dao.saveOrUpdate(user);

        // delete the token
        registrationTokenService.deleteTokenAfterActivation(token);

        logger.info("The user '" + user.getAccountName()
            + "' has successfully been activated.");
    }

    /**
     * Persists a new user in the database.
     *
     * @param user            The user to create
     * @param encryptPassword Whether or not the current password of the user object should
     *                        be encrypted or not before the object is persisted in the db
     * @return The persisted user object (incl. ID value)
     */
    public E persistNewUser(E user, boolean encryptPassword) {

        if (user.getId() != null) {
            // to be sure that we are in the
            // "create" case, the id must be null
            return user;
        }

        if (encryptPassword) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        dao.saveOrUpdate(user);

        return user;
    }

    /**
     * @param user
     * @param rawPassword
     * @throws Exception
     */
    public void updatePassword(E user, String rawPassword) throws Exception {

        if (user.getId() == null) {
            throw new Exception("The ID of the user object is null.");
        }

        user.setPassword(passwordEncoder.encode(rawPassword));
        dao.saveOrUpdate(user);
    }

    /**
     *
     */
    @Transactional(readOnly = true)
    public E getUserBySession() {

        final Object principal = SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();

        if (!(principal instanceof User)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        E loggedInUser = (E) principal;

        // The SecurityContextHolder holds a static copy of the user from
        // the moment he logged in. So we need to get the current instance from
        // dao.
        Integer id = loggedInUser.getId();

        return dao.findById(id);
    }

    /**
     * @param userId
     * @throws Exception
     */
    @PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public Set<UserGroup> getGroupsOfUser(Integer userId) throws Exception {
        E user = this.findById(userId);
        if (user != null) {
            logger.trace("Found user with ID " + user.getId());
            Set<UserGroup> userGroupsSet = user.getUserGroups();
            return userGroupsSet;
        } else {
            throw new Exception("The user with id " + userId + " could not be found");
        }

    }

    /**
     * @return the passwordEncoder
     */
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    /**
     * @param passwordEncoder the passwordEncoder to set
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @return the defaultUserRole
     */
    public Role getDefaultUserRole() {
        return defaultUserRole;
    }

    /**
     * @param defaultUserRole the defaultUserRole to set
     */
    public void setDefaultUserRole(Role defaultUserRole) {
        this.defaultUserRole = defaultUserRole;
    }

}
