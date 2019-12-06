package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.Territory;
import org.springframework.stereotype.Repository;

@Repository("territoryDao")
public class TerritoryDao<E extends Territory> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public TerritoryDao() {
        super((Class<E>) Territory.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected TerritoryDao(Class<E> clazz) {
        super(clazz);
    }

}
