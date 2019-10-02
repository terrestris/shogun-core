package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.layout.Layout;
import org.springframework.stereotype.Repository;

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
