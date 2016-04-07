package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.token.RegistrationToken;

@Repository("registrationTokenDao")
public class RegistrationTokenDao<E extends RegistrationToken> extends
		AbstractUserTokenDao<E> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public RegistrationTokenDao() {
		super((Class<E>) RegistrationToken.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected RegistrationTokenDao(Class<E> clazz) {
		super(clazz);
	}

}
