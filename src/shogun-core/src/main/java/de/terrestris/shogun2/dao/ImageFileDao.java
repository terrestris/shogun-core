package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.File;
import de.terrestris.shogun2.model.ImageFile;


/**
 * @author Johannes Weskamm
 */
@Repository("imageFileDao")
public class ImageFileDao<E extends File>
    extends FileDao<E> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public ImageFileDao() {
        super((Class<E>) ImageFile.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected ImageFileDao(Class<E> clazz) {
        super(clazz);
    }
}
