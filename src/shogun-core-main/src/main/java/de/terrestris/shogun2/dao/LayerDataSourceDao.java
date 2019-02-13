package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layer.source.LayerDataSource;

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
