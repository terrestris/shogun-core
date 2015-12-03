package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.Role;

@Repository
public class RoleDao extends GenericHibernateDao<Role, Integer> {

	protected RoleDao() {
		super(Role.class);
	}

}
