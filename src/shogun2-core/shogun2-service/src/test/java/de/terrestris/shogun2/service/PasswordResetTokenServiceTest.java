package de.terrestris.shogun2.service;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.PasswordResetToken;

/**
 * @author Nils Bühner
 *
 */
public class PasswordResetTokenServiceTest extends
		AbstractUserTokenServiceTest<PasswordResetToken> {

	@Override
	public void setUpUserTokenToUse() throws Exception {
		userTokenToUse = new PasswordResetToken(new User());
	}

	@Override
	protected AbstractUserTokenService<PasswordResetToken> getUserTokenService() {
		return new PasswordResetTokenService();
	}

	@Override
	protected PasswordResetToken getExpiredUserToken() {
		return new PasswordResetToken(new User(), -1);
	}

	@Override
	protected PasswordResetToken getUserTokenWithoutUser() {
		return new PasswordResetToken(null);
	}

}
