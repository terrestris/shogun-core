package de.terrestris.shoguncore.converter;

import de.terrestris.shoguncore.dao.ModuleDao;
import de.terrestris.shoguncore.model.module.Module;
import de.terrestris.shoguncore.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * An ID resolver for {@link Module}s when deserializing only on the base of ID values. Based on a given ID, this
 * resolver will load the whole entity from the database. Extends the default implementation.
 *
 * @author Andre Henn
 */
public class ModuleIdResolver<E extends Module, D extends ModuleDao<E>, S extends ModuleService<E, D>> extends
    PersistentObjectIdResolver<E, D, S> {

    @Override
    @Autowired
    @Qualifier("moduleService")
    public void setService(S service) {
        this.service = service;
    }

}
