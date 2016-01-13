package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.User;

@Repository("userDao")
public class UserDao<E extends User> extends PersonDao<E> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public UserDao() {
		super((Class<E>) User.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected UserDao(Class<E> clazz) {
		super(clazz);
	}

}
