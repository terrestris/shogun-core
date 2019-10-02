package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.module.Module;
import org.springframework.stereotype.Repository;

@Repository("moduleDao")
public class ModuleDao<E extends Module> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public ModuleDao() {
        super((Class<E>) Module.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected ModuleDao(Class<E> clazz) {
        super(clazz);
    }

}
