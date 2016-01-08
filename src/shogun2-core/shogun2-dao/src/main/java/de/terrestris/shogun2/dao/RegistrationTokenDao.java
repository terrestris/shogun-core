package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.token.RegistrationToken;

@Repository
public class RegistrationTokenDao extends GenericHibernateDao<RegistrationToken, Integer> {

	protected RegistrationTokenDao() {
		super(RegistrationToken.class);
	}

}
