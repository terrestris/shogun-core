package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.WpsReferenceDao;
import de.terrestris.shogun2.model.wps.WpsReference;

/**
 * Service class for the {@link WpsReference} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("wpsReferenceService")
public class WpsReferenceService<E extends WpsReference, D extends WpsReferenceDao<E>> extends
		WpsParameterService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public WpsReferenceService() {
		this((Class<E>) WpsReference.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected WpsReferenceService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("wpsReferenceDao")
	public void setDao(D dao) {
		this.dao = dao;
	}
}
