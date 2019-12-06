package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.layer.source.LayerDataSource;
import org.springframework.stereotype.Repository;

@Repository("layerDataSourceDao")
public class LayerDataSourceDao<E extends LayerDataSource> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public LayerDataSourceDao() {
        super((Class<E>) LayerDataSource.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected LayerDataSourceDao(Class<E> clazz) {
        super(clazz);
    }

}
