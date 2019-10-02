package de.terrestris.shoguncore.dao;

import de.terrestris.shoguncore.model.File;
import org.springframework.stereotype.Repository;


/**
 * @author Johannes Weskamm
 */
@Repository("fileDao")
public class FileDao<E extends File>
    extends GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public FileDao() {
        super((Class<E>) File.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected FileDao(Class<E> clazz) {
        super(clazz);
    }
}
