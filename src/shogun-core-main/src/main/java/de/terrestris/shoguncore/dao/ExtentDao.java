package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.layer.util.Extent;
import org.springframework.stereotype.Repository;

@Repository("extentDao")
public class ExtentDao<E extends Extent> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public ExtentDao() {
        super((Class<E>) Extent.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected ExtentDao(Class<E> clazz) {
        super(clazz);
    }

}
