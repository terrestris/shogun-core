package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.Application;

@Repository
public class ApplicationDao extends GenericHibernateDao<Application, Integer> {

	protected ApplicationDao() {
		super(Application.class);
	}

}
