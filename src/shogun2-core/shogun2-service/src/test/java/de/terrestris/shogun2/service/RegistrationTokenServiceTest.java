package de.terrestris.shogun2.service;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.RegistrationToken;

/**
 * @author Nils Bühner
 *
 */
public class RegistrationTokenServiceTest extends
		AbstractUserTokenServiceTest<RegistrationToken> {

	@Override
	public void setUpUserTokenToUse() throws Exception {
		userTokenToUse = new RegistrationToken(new User());
	}

	@Override
	protected AbstractUserTokenService<RegistrationToken> getUserTokenService() {
		return new RegistrationTokenService();
	}

	@Override
	protected RegistrationToken getExpiredUserToken() {
		return new RegistrationToken(new User(), -1);
	}

	@Override
	protected RegistrationToken getUserTokenWithoutUser() {
		return new RegistrationToken(null);
	}

}
