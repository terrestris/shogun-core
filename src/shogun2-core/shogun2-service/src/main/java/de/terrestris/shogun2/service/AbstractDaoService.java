package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.PersistentObject;

/**
 * This abstract service class simply provides a data access object for the type
 * {@link T}.
 * 
 * @author Nils Bühner
 * 
 */
@Transactional(readOnly = true)
public abstract class AbstractDaoService<T extends PersistentObject> {

	@Autowired
	protected GenericHibernateDao<T, Integer> dao;

}
