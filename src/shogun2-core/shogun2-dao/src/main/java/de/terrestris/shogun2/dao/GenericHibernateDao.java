package de.terrestris.shogun2.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.paging.PagingResult;

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
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		addCriterionsToCriteria(criteria, criterion);
		return criteria.list();
	}

	/**
	 * Gets the results, that match a variable number of passed criterions,
	 * considering the paging- and sort-info at the same time.
	 *
	 * @param firstResult
	 *            Starting index for the paging request.
	 * @param maxResults
	 *            Max number of result size.
	 * @param criterion
	 *            A variable number of hibernate criterions
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PagingResult<E> findByCriteriaWithSortingAndPaging(Integer firstResult,
			Integer maxResults, List<Order> sorters, Criterion... criterion) {
		Criteria criteria = getSession().createCriteria(clazz);

		// add paging info
		if (maxResults != null) {
			criteria.setMaxResults(maxResults);
		}
		if (firstResult != null) {
			criteria.setFirstResult(firstResult);
		}

		// add sort info
		if (sorters != null) {
			for (Order sortInfo : sorters) {
				criteria.addOrder(sortInfo);
			}
		}

		if(criterion != null) {
			addCriterionsToCriteria(criteria, criterion);
		}

		return new PagingResult<E>(criteria.list(), getTotalCount(criterion));
	}

	/**
	 * Returns the total count of db entries for the current type.
	 * @param criterion
	 *
	 * @return
	 */
	private Number getTotalCount(Criterion... criterion) {
		Criteria criteria = getSession().createCriteria(clazz);
		addCriterionsToCriteria(criteria, criterion);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	/**
	 *
	 * @param criteria
	 * @param criterion
	 */
	private void addCriterionsToCriteria(Criteria criteria, Criterion... criterion) {
		for (Criterion c : criterion) {
			if (c != null) {
				criteria.add(c);
			}
		}
	}
}
