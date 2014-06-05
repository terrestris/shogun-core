package de.terrestris.shogun2.service;

import org.springframework.stereotype.Service;

import de.terrestris.shogun2.model.Person;

/**
 * Service class for the {@link Person} model.
 * 
 * @author Nils Bühner
 * @see AbstractCrudService
 * 
 */
@Service("personService")
public class PersonService extends AbstractExtDirectCrudService<Person> {

}
