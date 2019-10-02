package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.wps.WpsParameter;
import org.springframework.stereotype.Repository;


/**
 * @author Nils Bühner
 */
@Repository("wpsParameterDao")
public class WpsParameterDao<E extends WpsParameter>
    extends GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public WpsParameterDao() {
        super((Class<E>) WpsParameter.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected WpsParameterDao(Class<E> clazz) {
        super(clazz);
    }
}
