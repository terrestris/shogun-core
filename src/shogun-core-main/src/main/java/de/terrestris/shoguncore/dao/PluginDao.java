package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.Plugin;
import org.springframework.stereotype.Repository;


/**
 * @author Nils Bühner
 */
@Repository("pluginDao")
public class PluginDao<E extends Plugin>
    extends GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public PluginDao() {
        super((Class<E>) Plugin.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected PluginDao(Class<E> clazz) {
        super(clazz);
    }
}
