package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.File;
import de.terrestris.shogun2.model.Image;


/**
 *
 * @author Johannes Weskamm
 *
 */
@Repository("imageDao")
public class ImageDao<E extends File>
		extends FileDao<E> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public ImageDao() {
		super((Class<E>) Image.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected ImageDao(Class<E> clazz) {
		super(clazz);
	}
}
