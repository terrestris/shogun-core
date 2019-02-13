package de.terrestris.shoguncore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shoguncore.dao.PersonDao;
import de.terrestris.shoguncore.model.Person;

/**
 * Service class for the {@link Person} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("personService")
public class PersonService<E extends Person, D extends PersonDao<E>> extends
    PermissionAwareCrudService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public PersonService() {
        this((Class<E>) Person.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected PersonService(Class<E> entityClass) {
        super(entityClass);
    }

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
