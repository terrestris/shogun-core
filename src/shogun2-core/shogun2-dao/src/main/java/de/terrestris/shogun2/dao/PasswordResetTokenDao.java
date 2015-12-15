package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.token.PasswordResetToken;

@Repository
public class PasswordResetTokenDao extends GenericHibernateDao<PasswordResetToken, Integer> {

	protected PasswordResetTokenDao() {
		super(PasswordResetToken.class);
	}

}
