package de.terrestris.shogun2.dao;

import de.terrestris.shogun2.model.storage.RootObject;
import org.springframework.stereotype.Repository;


/**
 * @author Johannes Weskamm
 */
@Repository("rootObjectDao")
public class RootObjectDao<E extends RootObject>
    extends GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public RootObjectDao() {
        super((Class<E>) RootObject.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected RootObjectDao(Class<E> clazz) {
        super(clazz);
    }
}
