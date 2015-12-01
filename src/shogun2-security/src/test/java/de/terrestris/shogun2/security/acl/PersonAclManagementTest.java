/**
 *
 */
package de.terrestris.shogun2.security.acl;

import org.springframework.beans.factory.annotation.Autowired;

import de.terrestris.shogun2.model.Person;
import de.terrestris.shogun2.service.AbstractCrudService;
import de.terrestris.shogun2.service.PersonService;

/**
 * @author Nils Bühner
 *
 */
public class PersonAclManagementTest extends
		AbstractAclManagementTest<Person> {

	@Autowired
	private PersonService personService;

	@Override
	protected Person getImplToUse() {
		return new Person();
	}

	@Override
	protected AbstractCrudService<Person> getCrudService() {
		return personService;
	}

}
