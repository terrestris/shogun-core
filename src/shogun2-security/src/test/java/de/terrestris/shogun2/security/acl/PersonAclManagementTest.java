/**
 *
 */
package de.terrestris.shogun2.security.acl;

import org.springframework.beans.factory.annotation.Autowired;

import de.terrestris.shogun2.dao.PersonDao;
import de.terrestris.shogun2.model.Person;
import de.terrestris.shogun2.service.AbstractCrudService;
import de.terrestris.shogun2.service.PersonService;

/**
 * @author Nils Bühner
 *
 */
public class PersonAclManagementTest extends
		AbstractAclManagementTest<Person, PersonDao<Person>> {

	@Autowired
	private PersonService<Person, PersonDao<Person>> personService;

	@Override
	protected Person getImplToUse() {
		return new Person();
	}

	@Override
	protected AbstractCrudService<Person, PersonDao<Person>> getCrudService() {
		return personService;
	}

}
