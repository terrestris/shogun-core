package de.terrestris.shogun2.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layer.Layer;
import de.terrestris.shogun2.model.module.Map;

@Repository("mapDao")
public class MapDao<E extends Map> extends
    ModuleDao<E> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public MapDao() {
        super((Class<E>) Map.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected MapDao(Class<E> clazz) {
        super(clazz);
    }

    /**
     *
     */
    public Set<E> findMapsWithLayer(Layer layer) throws HibernateException {
        final List<E> resultList = this.findAllWithCollectionContaining("mapLayers", layer);
        return new HashSet<>(resultList);
    }

}
