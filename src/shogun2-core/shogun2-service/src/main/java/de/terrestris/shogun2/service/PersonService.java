package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.PersonDao;
import de.terrestris.shogun2.model.Person;

/**
 * Service class for the {@link Person} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("personService")
public class PersonService<E extends Person, D extends PersonDao<E>> extends
		AbstractExtDirectCrudService<E, D> {

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("personDao")
	public void setDao(D dao) {
		this.dao = dao;
	}
}
