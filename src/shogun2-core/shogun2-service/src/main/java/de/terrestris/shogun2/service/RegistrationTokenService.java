package de.terrestris.shogun2.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.RegistrationToken;
import de.terrestris.shogun2.util.application.Shogun2ContextUtil;
import de.terrestris.shogun2.util.mail.MailPublisher;

/**
 *
 * @author Daniel Koch
 * @author Nils Bühner
 *
 */
@Service("registrationTokenService")
public class RegistrationTokenService extends AbstractUserTokenService<RegistrationToken> {

	/**
	 * The relative URL for the SHOGun2 registration activation interface.
	 */
	private static final String REGISTER_ACTIVATION_URL = "/register/activate.action";

	/**
	 * The Logger
	 */
	private static final Logger LOG =
			Logger.getLogger(RegistrationTokenService.class);

	/**
	 *
	 */
	@Autowired
	private MailPublisher mailPublisher;

	/**
	 *
	 */
	@Autowired
	@Qualifier("registrationMailMessageTemplate")
	private SimpleMailMessage registrationMailMessageTemplate;

	/**
	 *
	 * @param user
	 * @return
	 */
	public RegistrationToken findByUser(User user) {

		SimpleExpression eqUser = Restrictions.eq("user", user);

		RegistrationToken registrationToken =
				dao.findByUniqueCriteria(eqUser);

		return registrationToken;
	}

	/**
	 *
	 * @return
	 */
	public RegistrationToken findByTokenValue(String token) {

		Criterion criteria = Restrictions.eq("token", token);

		RegistrationToken registrationToken =
				dao.findByUniqueCriteria(criteria);

		return registrationToken;
	}

	/**
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 *
	 */
	public void sendRegistrationActivationMail(HttpServletRequest request,
			User user) throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			URISyntaxException, UnsupportedEncodingException {

		// generate and save the unique registration token for the user
		RegistrationToken registrationToken = getValidTokenForUser(user);

		// create the reset-password URI that will be send to the user
		URI resetPasswordURI = createRegistrationActivationURI(request,
				registrationToken);

		// create a thread safe "copy" of the template message
		SimpleMailMessage registrationActivationMsg = new SimpleMailMessage(
				registrationMailMessageTemplate);

		// prepare a personalized mail with the given token
		final String email = user.getEmail();
		registrationActivationMsg.setTo(email);
		registrationActivationMsg.setText(
				String.format(
						registrationActivationMsg.getText(),
						UriUtils.decode(resetPasswordURI.toString(), "UTF-8")
				)
		);

		// and send the mail
		mailPublisher.sendMail(registrationActivationMsg);

		LOG.debug("Sent activation mail to " + email);

	}

	/**
	 *
	 * @param request
	 * @param registrationToken
	 * @return
	 * @throws URISyntaxException
	 */
	private URI createRegistrationActivationURI(HttpServletRequest request,
			RegistrationToken registrationToken) throws URISyntaxException {

		// get the webapp URI
		URI appURI = Shogun2ContextUtil.getApplicationURIFromRequest(request);

		// build the registration activation link URI
		URI tokenURI = new URIBuilder(appURI)
				.setPath(appURI.getPath() + REGISTER_ACTIVATION_URL)
				.setParameter("token", registrationToken.getToken())
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
	 * @return the registrationMailMessageTemplate
	 */
	public SimpleMailMessage getRegistrationMailMessageTemplate() {
		return registrationMailMessageTemplate;
	}

	/**
	 * @param registrationMailMessageTemplate the registrationMailMessageTemplate to set
	 */
	public void setRegistrationMailMessageTemplate(
			SimpleMailMessage registrationMailMessageTemplate) {
		this.registrationMailMessageTemplate = registrationMailMessageTemplate;
	}


}
