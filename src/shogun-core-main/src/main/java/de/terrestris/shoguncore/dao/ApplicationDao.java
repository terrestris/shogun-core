package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.Application;
import org.springframework.stereotype.Repository;

@Repository("applicationDao")
public class ApplicationDao<E extends Application> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public ApplicationDao() {
        super((Class<E>) Application.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected ApplicationDao(Class<E> clazz) {
        super(clazz);
    }

}
