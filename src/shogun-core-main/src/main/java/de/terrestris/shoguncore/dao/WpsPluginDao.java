package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.wps.WpsPlugin;
import org.springframework.stereotype.Repository;


/**
 * @author Nils Bühner
 */
@Repository("wpsPluginDao")
public class WpsPluginDao<E extends WpsPlugin> extends PluginDao<E> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public WpsPluginDao() {
        super((Class<E>) WpsPlugin.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected WpsPluginDao(Class<E> clazz) {
        super(clazz);
    }
}
