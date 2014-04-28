package de.terrestris.shogun2.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * @author Nils Bühner
 * 
 */
public abstract class GenericHibernateDao<E extends PersistentObject, ID extends Serializable> {

	/**
	 * Represents the class of the entity
	 */
	private Class<E> clazz;

	protected GenericHibernateDao(Class<E> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Hibernate SessionFactory
	 */
	@Autowired
	private SessionFactory sessionFactory;

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public E findById(ID id) {
		return (E) getSession().get(clazz, id);
	}

	/**
	 * Returns all Entities by calling findByCriteria(), i.e. without arguments.
	 * 
	 * @see GenericHibernateDao#findByCriteria(Criterion...)
	 * @return All entities
	 */
	public List<E> findAll() {
		return findByCriteria();
	}

	public void saveOrUpdate(E e) {
		e.setModified(DateTime.now());
		getSession().saveOrUpdate(e);
	}

	public void delete(E e) {
		getSession().delete(e);
	}

	/**
	 * Gets the results, that match a variable number of passed criterions. Call
	 * this method without arguments to find all entities.
	 * 
	 * @param criterion
	 *            A variable number of hibernate criterions
	 * @return Entities matching the passed hibernate criterions
	 */
	@SuppressWarnings("unchecked")
	public List<E> findByCriteria(Criterion... criterion) {
		Criteria criteria = getSession().createCriteria(clazz);
		for (Criterion c : criterion) {
			criteria.add(c);
		}
		return criteria.list();
	}

}
