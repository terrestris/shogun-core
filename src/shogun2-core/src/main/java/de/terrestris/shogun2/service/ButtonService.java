package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.ButtonDao;
import de.terrestris.shogun2.model.module.Button;

/**
 * Service class for the {@link Button} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("buttonService")
public class ButtonService<E extends Button, D extends ButtonDao<E>> extends
		PermissionAwareCrudService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public ButtonService() {
		this((Class<E>) Button.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected ButtonService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("buttonDao")
	public void setDao(D dao) {
		this.dao = dao;
	}
}
