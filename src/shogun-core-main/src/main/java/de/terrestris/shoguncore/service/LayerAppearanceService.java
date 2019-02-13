package de.terrestris.shoguncore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shoguncore.dao.LayerAppearanceDao;
import de.terrestris.shoguncore.model.layer.appearance.LayerAppearance;
import de.terrestris.shoguncore.model.module.Module;

/**
 * Service class for the {@link Module} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("layerAppearanceService")
public class LayerAppearanceService<E extends LayerAppearance, D extends LayerAppearanceDao<E>> extends
    PermissionAwareCrudService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public LayerAppearanceService() {
        this((Class<E>) LayerAppearance.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected LayerAppearanceService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("layerAppearanceDao")
    public void setDao(D dao) {
        this.dao = dao;
    }
}
