package de.terrestris.shogun2.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.model.security.PermissionCollection;
import de.terrestris.shogun2.paging.PagingResult;

/**
 * The superclass for all data access objects. Provides basic CRUD
 * functionality and a logger instance for all subclasses.
 *
 * @author Nils Bühner
 *
 */
@Repository("genericDao")
public class GenericHibernateDao<E extends PersistentObject, ID extends Serializable> {

	/**
	 * The LOGGER instance (that will be available in all subclasses)
	 */
	protected final Logger LOG = Logger.getLogger(getClass());

	/**
	 * Represents the class of the entity
	 */
	private final Class<E> entityClass;

	/**
	 * Default constructor
	 */
	@SuppressWarnings("unchecked")
	public GenericHibernateDao() {
		this((Class<E>) PersistentObject.class);
	}

	/**
	 * Constructor
	 *
	 * @param clazz
	 */
	protected GenericHibernateDao(Class<E> clazz) {
		this.entityClass = clazz;
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
		LOG.trace("Finding " + entityClass.getSimpleName() + " with ID " + id);
		return (E) getSession().get(entityClass, id);
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
		LOG.trace("Loading " + entityClass.getSimpleName() + " with ID " + id);
		return (E) getSession().load(entityClass, id);
	}

	/**
	 * Returns all Entities by calling findByCriteria(), i.e. without arguments.
	 *
	 * @see GenericHibernateDao#findByCriteria(Criterion...)
	 * @return All entities
	 */
	public List<E> findAll() throws HibernateException {
		LOG.trace("Finding all instances of " + entityClass.getSimpleName());
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

		LOG.trace(createOrUpdatePrefix + " instance of " + entityClass.getSimpleName()
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
		LOG.trace("Deleting " + entityClass.getSimpleName() + " with ID " + e.getId());
		getSession().delete(e);
	}

	/**
	 * Evicts/detaches the entity from the hibernate session cache.
	 *
	 * @param e The entity to evict/detach
	 */
	public void evict(E e) {
		LOG.trace("Removing/detaching " + entityClass.getSimpleName() + " with ID " + e.getId()
				+ " from the hibernate session cache.");
		getSession().evict(e);
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
		LOG.trace("Finding instances of " + entityClass.getSimpleName()
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
		LOG.trace("Finding one unique " + entityClass.getSimpleName()
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

		LOG.trace("Finding instances of " + entityClass.getSimpleName()
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
	 * This method returns a {@link Map} that maps {@link PersistentObject}s
	 * to PermissionCollections for the passed {@link User}. I.e. the keySet
	 * of the map is the collection of all {@link PersistentObject}s where the
	 * user has at least one permission and the corresponding value contains
	 * the {@link PermissionCollection} for the passed user on the entity.
	 *
	 * @param user
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public Map<PersistentObject, PermissionCollection> findAllUserPermissionsOfUser(User user) {

		Criteria criteria = getSession().createCriteria(PersistentObject.class);

		// by only setting the alias, we will only get those entities where
		// there is at least one permission set...
		// it is hard (or even impossible in this scenario) to create a
		// restriction that filters for permissions of the given user only.
		// using HQL here is no option as the PersistentObject is
		// a MappedSuperclass (without table).
		// another efficient way would be a SQL query, but then the SQL
		// would be written in an explicit SQL dialect...
		criteria.createAlias("userPermissions", "up");
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<PersistentObject> entitiesWithPermissions = criteria.list();

		Map<PersistentObject, PermissionCollection> userPermissions = new HashMap<PersistentObject, PermissionCollection>();

		// TODO find a better way than iterating over all entities of the system
		// that have at least one permission (for any user) (see comment above)
		for (PersistentObject entity : entitiesWithPermissions) {
			Map<User, PermissionCollection> entityUserPermissions = entity.getUserPermissions();
			if(entityUserPermissions.containsKey(user)) {
				userPermissions.put(entity, entityUserPermissions.get(user));
			}
		}

		return userPermissions;
	}

	/**
	 * This method returns a {@link Map} that maps {@link PersistentObject}s
	 * to PermissionCollections for the passed {@link UserGroup}. I.e. the keySet
	 * of the map is the collection of all {@link PersistentObject}s where the
	 * user group has at least one permission and the corresponding value contains
	 * the {@link PermissionCollection} for the passed user group on the entity.
	 *
	 * @param userGroup
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public Map<PersistentObject, PermissionCollection> findAllUserGroupPermissionsOfUserGroup(UserGroup userGroup) {

		Criteria criteria = getSession().createCriteria(PersistentObject.class);

		// by only setting the alias, we will only get those entities where
		// there is at least one permission set...
		// it is hard (or even impossible in this scenario) to create a
		// restriction that filters for permissions of the given user group only.
		// using HQL here is no option as the PersistentObject is
		// a MappedSuperclass (without table).
		// another efficient way would be a SQL query, but then the SQL
		// would be written in an explicit SQL dialect...
		criteria.createAlias("groupPermissions", "gp");
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<PersistentObject> entitiesWithPermissions = criteria.list();

		Map<PersistentObject, PermissionCollection> userGroupPermissions = new HashMap<PersistentObject, PermissionCollection>();

		// TODO find a better way than iterating over all entities of the system
		// that have at least one permission (for any user) (see comment above)
		for (PersistentObject entity : entitiesWithPermissions) {
			Map<UserGroup, PermissionCollection> entityUserGroupPermissions = entity.getGroupPermissions();
			if(entityUserGroupPermissions.containsKey(userGroup)) {
				userGroupPermissions.put(entity, entityUserGroupPermissions.get(userGroup));
			}
		}

		return userGroupPermissions;
	}

	/**
	 * Helper method: Creates a criteria for the {@link #entityClass} of this dao.
	 * The query results will be handled with a
	 * {@link DistinctRootEntityResultTransformer}. The criteria will contain
	 * all passed criterions.
	 *
	 * @return
	 */
	protected Criteria createDistinctRootEntityCriteria(Criterion... criterion) {
		Criteria criteria = getSession().createCriteria(entityClass);
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
		Criteria criteria = getSession().createCriteria(entityClass);
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

	/**
	 * @return the entityClass
	 */
	public Class<E> getEntityClass() {
		return entityClass;
	}

}
