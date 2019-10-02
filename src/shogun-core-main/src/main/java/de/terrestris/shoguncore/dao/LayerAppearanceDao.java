package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.layer.appearance.LayerAppearance;
import org.springframework.stereotype.Repository;

@Repository("layerAppearanceDao")
public class LayerAppearanceDao<E extends LayerAppearance> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public LayerAppearanceDao() {
        super((Class<E>) LayerAppearance.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected LayerAppearanceDao(Class<E> clazz) {
        super(clazz);
    }

}
