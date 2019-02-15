package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.PasswordResetTokenDao;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.token.PasswordResetToken;

/**
 * @author Nils Bühner
 */
public class PasswordResetTokenServiceTest extends
    AbstractUserTokenServiceTest<PasswordResetToken, PasswordResetTokenDao<PasswordResetToken>, PasswordResetTokenService<PasswordResetToken, PasswordResetTokenDao<PasswordResetToken>>> {

    @Override
    protected PasswordResetTokenService<PasswordResetToken, PasswordResetTokenDao<PasswordResetToken>> getCrudService() {
        return new PasswordResetTokenService<PasswordResetToken, PasswordResetTokenDao<PasswordResetToken>>();
    }

    @Override
    protected PasswordResetToken getExpiredUserToken() {
        return new PasswordResetToken(new User(), -1);
    }

    @Override
    protected PasswordResetToken getUserTokenWithoutUser() {
        return new PasswordResetToken(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<PasswordResetTokenDao<PasswordResetToken>> getDaoClass() {
        return (Class<PasswordResetTokenDao<PasswordResetToken>>) new PasswordResetTokenDao<PasswordResetToken>()
            .getClass();
    }

    @Override
    public void setUpImplToTest() throws Exception {
        implToTest = new PasswordResetToken(new User());
    }

}
