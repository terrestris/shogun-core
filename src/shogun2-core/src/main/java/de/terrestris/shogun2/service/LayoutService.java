package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.LayoutDao;
import de.terrestris.shogun2.model.layout.Layout;

/**
 * Service class for the {@link Layout} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("layoutService")
public class LayoutService<E extends Layout, D extends LayoutDao<E>> extends
		AbstractPermissionAwareCrudService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public LayoutService() {
		this((Class<E>) Layout.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected LayoutService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("layoutDao")
	public void setDao(D dao) {
		this.dao = dao;
	}
}
