package de.terrestris.shogun2.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.DistinctRootEntityResultTransformer;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.paging.PagingResult;

/**
 * The abstract superclass for all data access objects. Provides basic CRUD
 * functionality and a logger instance for all subclasses.
 *
 * @author Nils Bühner
 *
 */
public abstract class GenericHibernateDao<E extends PersistentObject, ID extends Serializable> {

	/**
	 * The LOGGER instance (that will be available in all subclasses)
	 */
	protected final Logger LOG = Logger.getLogger(getClass());

	/**
	 * Represents the class of the entity
	 */
	private Class<E> clazz;

	/**
	 * Constructor
	 *
	 * @param clazz
	 */
	protected GenericHibernateDao(Class<E> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Hibernate SessionFactory
	 */
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Obtains the current session.
	 *
	 * @return
	 */
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * Return the real object from the database. Returns null if the object does
	 * not exist.
	 *
	 * @param id
	 *
	 * @see http://www.mkyong.com/hibernate/different-between-session-get-and-session-load/
	 *
	 * @return The object from the database or null if it does not exist
	 */
	public E findById(ID id) {
		LOG.trace("Finding " + clazz.getSimpleName() + " with ID " + id);
		return (E) getSession().get(clazz, id);
	}

	/**
	 * Return a proxy of the object (without hitting the database). This should
	 * only be used if it is assumed that the object really exists and where
	 * non-existence would be an actual error.
	 *
	 * @param id
	 *
	 * @see http://www.mkyong.com/hibernate/different-between-session-get-and-session-load/
	 *
	 * @return
	 */
	public E loadById(ID id) {
		LOG.trace("Loading " + clazz.getSimpleName() + " with ID " + id);
		return (E) getSession().load(clazz, id);
	}

	/**
	 * Returns all Entities by calling findByCriteria(), i.e. without arguments.
	 *
	 * @see GenericHibernateDao#findByCriteria(Criterion...)
	 * @return All entities
	 */
	public List<E> findAll() throws HibernateException {
		LOG.trace("Finding all instances of " + clazz.getSimpleName());
		return findByCriteria();
	}

	/**
	 * Saves or updates the passed entity.
	 *
	 * @param e The entity to save or update in the database.
	 */
	public void saveOrUpdate(E e) {
		final Integer id = e.getId();
		final boolean hasId = id != null;
		String createOrUpdatePrefix = hasId ? "Updating" : "Creating a new";
		String idSuffix = hasId ? "with ID " + id : "";

		LOG.trace(createOrUpdatePrefix + " instance of " + clazz.getSimpleName()
				+ idSuffix);

		e.setModified(DateTime.now());
		getSession().saveOrUpdate(e);
	}

	/**
	 * Deletes the passed entity.
	 *
	 * @param e The entity to remove from the database.
	 */
	public void delete(E e) {
		LOG.trace("Deleting " + clazz.getSimpleName() + " with ID " + e.getId());
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
	public List<E> findByCriteria(Criterion... criterion) throws HibernateException {
		LOG.trace("Finding all instances of " + clazz.getSimpleName()
				+ " based on " + criterion.length + " criteria");

		Criteria criteria = createDistinctRootEntityCriteria(criterion);
		return criteria.list();
	}

	/**
	 * Gets the unique result, that matches a variable number of passed
	 * criterions.
	 *
	 * @param criterion
	 *            A variable number of hibernate criterions
	 * @return Entity matching the passed hibernate criterions
	 *
	 * @throws HibernateException if there is more than one matching result
	 */
	@SuppressWarnings("unchecked")
	public E findByUniqueCriteria(Criterion... criterion) throws HibernateException {
		LOG.trace("Finding one unique " + clazz.getSimpleName()
				+ " based on " + criterion.length + " criteria");

		Criteria criteria = createDistinctRootEntityCriteria(criterion);
		return (E) criteria.uniqueResult();
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
			Integer maxResults, List<Order> sorters, Criterion... criterion) throws HibernateException {

		int nrOfSorters = sorters == null ? 0 : sorters.size();

		LOG.trace("Finding instances of " + clazz.getSimpleName()
				+ " based on " + criterion.length + " criteria"
				+ " with " + nrOfSorters + " sorters");

		Criteria criteria = createDistinctRootEntityCriteria(criterion);

		// add paging info
		if (maxResults != null) {
			LOG.trace("Limiting result set size to " + maxResults);
			criteria.setMaxResults(maxResults);
		}
		if (firstResult != null) {
			LOG.trace("Setting the first result to be retrieved to "
					+ firstResult);
			criteria.setFirstResult(firstResult);
		}

		// add sort info
		if (sorters != null) {
			for (Order sortInfo : sorters) {
				criteria.addOrder(sortInfo);
			}
		}

		return new PagingResult<E>(criteria.list(), getTotalCount(criterion));
	}

	/**
	 * Helper method: Creates a criteria for the {@link #clazz} of this dao.
	 * The query results will be handled with a
	 * {@link DistinctRootEntityResultTransformer}. The criteria will contain
	 * all passed criterions.
	 *
	 * @return
	 */
	protected Criteria createDistinctRootEntityCriteria(Criterion... criterion) {
		Criteria criteria = getSession().createCriteria(clazz);
		addCriterionsToCriteria(criteria, criterion);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria;
	}

	/**
	 * Returns the total count of db entries for the current type.
	 * @param criterion
	 *
	 * @return
	 */
	private Number getTotalCount(Criterion... criterion) throws HibernateException {
		Criteria criteria = getSession().createCriteria(clazz);
		addCriterionsToCriteria(criteria, criterion);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	/**
	 * Helper method: Adds all criterions to the criteria (if not null).
	 *
	 * @param criteria
	 * @param criterion
	 */
	private void addCriterionsToCriteria(Criteria criteria, Criterion... criterion) {
		if(criteria != null) {
			for (Criterion c : criterion) {
				if (c != null) {
					criteria.add(c);
				}
			}
		}
	}
}
