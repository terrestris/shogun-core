package de.terrestris.shoguncore.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shoguncore.model.wps.WpsProcessExecute;


/**
 * @author Nils Bühner
 */
@Repository("wpsProcessExecuteDao")
public class WpsProcessExecuteDao<E extends WpsProcessExecute> extends WpsReferenceDao<E> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public WpsProcessExecuteDao() {
        super((Class<E>) WpsProcessExecute.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected WpsProcessExecuteDao(Class<E> clazz) {
        super(clazz);
    }
}
