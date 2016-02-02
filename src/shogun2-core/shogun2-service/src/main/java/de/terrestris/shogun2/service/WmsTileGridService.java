package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.WmsTileGridDao;
import de.terrestris.shogun2.model.layer.util.WmsTileGrid;
import de.terrestris.shogun2.model.module.Module;

/**
 * Service class for the {@link Module} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("wmsTileGridService")
public class WmsTileGridService<E extends WmsTileGrid, D extends WmsTileGridDao<E>> extends
		AbstractCrudService<E, D> {

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("wmsTileGridDao")
	public void setDao(D dao) {
		this.dao = dao;
	}
}
