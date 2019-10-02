package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.layer.Layer;
import org.springframework.stereotype.Repository;

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
