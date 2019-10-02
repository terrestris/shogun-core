package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.WpsPluginDao;
import de.terrestris.shoguncore.model.wps.WpsPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service class for the {@link WpsPlugin} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("wpsPluginService")
public class WpsPluginService<E extends WpsPlugin, D extends WpsPluginDao<E>> extends
    PluginService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public WpsPluginService() {
        this((Class<E>) WpsPlugin.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected WpsPluginService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("wpsPluginDao")
    public void setDao(D dao) {
        this.dao = dao;
    }
}
