package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.ApplicationDao;
import de.terrestris.shogun2.model.Application;

/**
 * Service class for the {@link Application} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("applicationService")
public class ApplicationService<E extends Application, D extends ApplicationDao<E>>
		extends AbstractPermissionAwareCrudService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public ApplicationService() {
		this((Class<E>) Application.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected ApplicationService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("applicationDao")
	public void setDao(D dao) {
		this.dao = dao;
	}
}
