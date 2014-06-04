package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.Person;

@Repository
public class PersonDao extends GenericHibernateDao<Person, Integer> {

	protected PersonDao() {
		super(Person.class);
	}

}
