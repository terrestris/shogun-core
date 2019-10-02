package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.Person;
import org.springframework.stereotype.Repository;

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
