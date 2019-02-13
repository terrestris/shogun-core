package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layout.Layout;

@Repository("layoutDao")
public class LayoutDao<E extends Layout> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public LayoutDao() {
        super((Class<E>) Layout.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected LayoutDao(Class<E> clazz) {
        super(clazz);
    }

}
