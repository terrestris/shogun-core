package de.terrestris.shoguncore.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shoguncore.model.layer.util.Extent;

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
