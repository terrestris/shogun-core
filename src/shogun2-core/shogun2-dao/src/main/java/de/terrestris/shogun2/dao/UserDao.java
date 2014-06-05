package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.User;

@Repository
public class UserDao extends GenericHibernateDao<User, Integer> {

	protected UserDao() {
		super(User.class);
	}

}
