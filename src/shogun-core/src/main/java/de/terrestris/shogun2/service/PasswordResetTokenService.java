package de.terrestris.shogun2.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import de.terrestris.shogun2.dao.PasswordResetTokenDao;
import de.terrestris.shogun2.dao.UserDao;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.PasswordResetToken;
import de.terrestris.shogun2.util.application.ShogunCoreContextUtil;
import de.terrestris.shogun2.util.mail.MailPublisher;

/**
 * @author Daniel Koch
 * @author Nils Bühner
 */
@Service("passwordResetTokenService")
public class PasswordResetTokenService<E extends PasswordResetToken, D extends PasswordResetTokenDao<E>>
    extends AbstractUserTokenService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public PasswordResetTokenService() {
        this((Class<E>) PasswordResetToken.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected PasswordResetTokenService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     *
     */
    @Autowired
    private UserService<User, UserDao<User>> userService;

    /**
     *
     */
    @Autowired
    private UserDao<User> userDao;

    /**
     *
     */
    @Autowired
    private MailPublisher mailPublisher;

    /**
     * The autowired PasswordEncoder
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     *
     */
    @Autowired
    @Qualifier("resetPasswordMailMessageTemplate")
    private SimpleMailMessage resetPasswordMailMessageTemplate;

    /**
     *
     */
    @Autowired
    private String changePasswordPath;

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("passwordResetTokenDao")
    public void setDao(D dao) {
        this.dao = dao;
    }

    /**
     * Builds a concrete instance of this class.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected E buildConcreteInstance(User user, Integer expirationTimeInMinutes) {
        if (expirationTimeInMinutes == null) {
            return (E) new PasswordResetToken(user);
        }
        return (E) new PasswordResetToken(user, expirationTimeInMinutes);
    }

    /**
     * @param request
     * @param email
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    public void sendResetPasswordMail(HttpServletRequest request, String email)
        throws NoSuchMethodException, SecurityException,
        InstantiationException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException,
        URISyntaxException, UnsupportedEncodingException {

        // get the user by the provided email address
        User user = userDao.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException(
                "Could not find user with email: '" + email + "'");
        }

        // generate and save the unique reset-password token for the user
        PasswordResetToken resetPasswordToken = getValidTokenForUser(user, null);

        // create the reset-password URI that will be send to the user
        URI resetPasswordURI = createResetPasswordURI(request,
            resetPasswordToken);

        // create a thread safe "copy" of the template message
        SimpleMailMessage resetPwdMsg = new SimpleMailMessage(
            resetPasswordMailMessageTemplate);

        // prepare a personalized mail with the given token
        resetPwdMsg.setTo(email);
        resetPwdMsg.setText(
            String.format(
                resetPwdMsg.getText(),
                user.getFirstName(),
                user.getLastName(),
                UriUtils.decode(resetPasswordURI.toString(), "UTF-8")
            )
        );

        // and send the mail
        mailPublisher.sendMail(resetPwdMsg);

    }

    /**
     * @param rawPassword
     * @param token
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void validateTokenAndUpdatePassword(String rawPassword, String token) throws Exception {

        // try to find the provided token
        PasswordResetToken passwordResetToken = findByTokenValue(token);

        // this would throw an exception if the token is not valid
        this.validateToken((E) passwordResetToken);

        // the user's password can be changed now

        // get the user of the provided token
        User user = passwordResetToken.getUser();

        // finally update the password (encrypted)
        userService.updatePassword(user, rawPassword);

        // delete the token
        dao.delete((E) passwordResetToken);

        LOG.trace("Deleted the token.");
        LOG.debug("Successfully updated the password.");

    }

    /**
     * @param request
     * @param resetPasswordToken
     * @return
     * @throws URISyntaxException
     */
    private URI createResetPasswordURI(HttpServletRequest request,
                                       PasswordResetToken resetPasswordToken) throws URISyntaxException {

        // get the webapp URI
        URI appURI = ShogunCoreContextUtil.getApplicationURIFromRequest(request);

        // build the change-password URI send to the user
        URI tokenURI = new URIBuilder(appURI)
            .setPath(appURI.getPath() + changePasswordPath)
            .setParameter("token", resetPasswordToken.getToken())
            .build();

        return tokenURI;
    }

    /**
     * @return the mailPublisher
     */
    public MailPublisher getMailPublisher() {
        return mailPublisher;
    }

    /**
     * @param mailPublisher the mailPublisher to set
     */
    public void setMailPublisher(MailPublisher mailPublisher) {
        this.mailPublisher = mailPublisher;
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
     * @return the resetPasswordMailMessageTemplate
     */
    public SimpleMailMessage getResetPasswordMailMessageTemplate() {
        return resetPasswordMailMessageTemplate;
    }

    /**
     * @param resetPasswordMailMessageTemplate the resetPasswordMailMessageTemplate to set
     */
    public void setResetPasswordMailMessageTemplate(
        SimpleMailMessage resetPasswordMailMessageTemplate) {
        this.resetPasswordMailMessageTemplate = resetPasswordMailMessageTemplate;
    }

    /**
     * @return the changePasswordPath
     */
    public String getChangePasswordPath() {
        return changePasswordPath;
    }

    /**
     * @param changePasswordPath the changePasswordPath to set
     */
    public void setChangePasswordPath(String changePasswordPath) {
        this.changePasswordPath = changePasswordPath;
    }

}
