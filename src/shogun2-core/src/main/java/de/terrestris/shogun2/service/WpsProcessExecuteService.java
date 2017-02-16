package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.WpsProcessExecuteDao;
import de.terrestris.shogun2.model.wps.WpsProcessExecute;

/**
 * Service class for the {@link WpsProcessExecute} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("wpsProcessExecuteService")
public class WpsProcessExecuteService<E extends WpsProcessExecute, D extends WpsProcessExecuteDao<E>> extends
		WpsReferenceService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public WpsProcessExecuteService() {
		this((Class<E>) WpsProcessExecute.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected WpsProcessExecuteService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("wpsProcessExecuteDao")
	public void setDao(D dao) {
		this.dao = dao;
	}
}
