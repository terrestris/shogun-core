package de.terrestris.shogun2.service;

import de.terrestris.shogun2.dao.RegistrationTokenDao;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.token.RegistrationToken;

/**
 * @author Nils Bühner
 */
public class RegistrationTokenServiceTest extends
    AbstractUserTokenServiceTest<RegistrationToken, RegistrationTokenDao<RegistrationToken>, RegistrationTokenService<RegistrationToken, RegistrationTokenDao<RegistrationToken>>> {

    @Override
    protected RegistrationTokenService<RegistrationToken, RegistrationTokenDao<RegistrationToken>> getCrudService() {
        return new RegistrationTokenService<RegistrationToken, RegistrationTokenDao<RegistrationToken>>();
    }

    @Override
    protected RegistrationToken getExpiredUserToken() {
        return new RegistrationToken(new User(), -1);
    }

    @Override
    protected RegistrationToken getUserTokenWithoutUser() {
        return new RegistrationToken(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<RegistrationTokenDao<RegistrationToken>> getDaoClass() {
        return (Class<RegistrationTokenDao<RegistrationToken>>) new RegistrationTokenDao<RegistrationToken>().getClass();
    }

    @Override
    public void setUpImplToTest() throws Exception {
        implToTest = new RegistrationToken(new User());
    }

}
