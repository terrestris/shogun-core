package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.Person;

@Repository("personDao")
public class PersonDao<E extends Person> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public PersonDao() {
        super((Class<E>) Person.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected PersonDao(Class<E> clazz) {
        super(clazz);
    }

}
