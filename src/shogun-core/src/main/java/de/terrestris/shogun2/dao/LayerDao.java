package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layer.Layer;

/**
 * @param <E>
 * @author Nils Bühner
 */
@Repository("layerDao")
public class LayerDao<E extends Layer> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public LayerDao() {
        super((Class<E>) Layer.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected LayerDao(Class<E> clazz) {
        super(clazz);
    }

}
