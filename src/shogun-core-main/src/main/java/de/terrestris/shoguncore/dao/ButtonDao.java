package de.terrestris.shoguncore.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shoguncore.model.module.Button;

@Repository("buttonDao")
public class ButtonDao<E extends Button> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public ButtonDao() {
        super((Class<E>) Button.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected ButtonDao(Class<E> clazz) {
        super(clazz);
    }

}
