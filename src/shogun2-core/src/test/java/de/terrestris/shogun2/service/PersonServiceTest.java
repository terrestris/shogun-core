package de.terrestris.shogun2.service;

import de.terrestris.shogun2.dao.PersonDao;
import de.terrestris.shogun2.model.Person;

public class PersonServiceTest extends
		AbstractSecuredPersistentObjectServiceTest<Person, PersonDao<Person>, PersonService<Person, PersonDao<Person>>> {

	/**
	 *
	 * @throws Exception
	 */
	public void setUpImplToTest() throws Exception {
		implToTest = new Person();
	}

	@Override
	protected PersonService<Person, PersonDao<Person>> getCrudService() {
		return new PersonService<Person, PersonDao<Person>>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<PersonDao<Person>> getDaoClass() {
		return (Class<PersonDao<Person>>) new PersonDao<Person>().getClass();
	}

}
