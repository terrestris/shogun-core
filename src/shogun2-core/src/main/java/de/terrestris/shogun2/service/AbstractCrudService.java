package de.terrestris.shogun2.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.util.entity.EntityUtil;

/**
 * This abstract service class provides basic CRUD functionality.
 *
 * @author Nils Bühner
 * @see AbstractDaoService
 *
 */
public abstract class AbstractCrudService<E extends PersistentObject, D extends GenericHibernateDao<E, Integer>>
		extends AbstractDaoService<E, D> {

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected AbstractCrudService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 *
	 * @param e
	 * @return
	 */
	@PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName())"
			+ " or (#e.id == null and hasPermission(#e, 'CREATE'))"
			+ " or (#e.id != null and hasPermission(#e, 'UPDATE'))")
	public void saveOrUpdate(E e) {
		dao.saveOrUpdate(e);
	}

	/**
	 * @param jsonObject
	 * @param entity
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	@PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#entity, 'UPDATE')")
	public E updatePartialWithJsonNode(E entity, JsonNode jsonObject, ObjectMapper objectMapper) throws IOException, JsonProcessingException {
		// update "partially". credits go to http://stackoverflow.com/a/15145480
		entity = objectMapper.readerForUpdating(entity).readValue(jsonObject);
		this.saveOrUpdate(entity);
		return entity;
	}

	/**
	 * Return the real object from the database. Returns null if the object does
	 * not exist.
	 *
	 * @param id
	 * @return
	 */
	@PostAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(returnObject, 'READ')")
	public E findById(Integer id) {
		return dao.findById(id);
	}

	/**
	 * Return a proxy of the object (without hitting the database). This should
	 * only be used if it is assumed that the object really exists and where
	 * non-existence would be an actual error.
	 *
	 * @param id
	 * @return
	 */
	@PostAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(returnObject, 'READ')")
	public E loadById(int id) {
		return dao.loadById(id);
	}

	/**
	 *
	 * @return
	 */
	@PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(filterObject, 'READ')")
	public List<E> findAll() {
		return dao.findAll();
	}

	/**
	 * Finds all entities that match the given filter (multi value map).
	 *
	 * Each key in the multi value map is the name of a field/property of the
	 * entity. These values may be case insensitive! Each field name is mapped
	 * to a list of values (passed as Strings).
	 *
	 * Fields that do not exist in the entity will be ignored.
	 *
	 * Example:
	 *
	 * int=['1','2']
	 * string=['foo']
	 * bool1=['0']
	 * bool2=['true']
	 *
	 * would be translated to a filter like (values are casted to the target type of the field)
	 *
	 * (int == 1 || int == 2) && (string == 'foo') && (bool1 == false) && (bool2 == true)
	 *
	 * and return all entities that match this condition.
	 *
	 * This will only work on simple properties of an entity.
	 *
	 * @param requestedFilter
	 * @return
	 */
	@PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(filterObject, 'READ')")
	public List<E> findBySimpleFilter(MultiValueMap<String,String> requestedFilter) {

		MultiValueMap<String, Object> origFieldNamesToCastedValues = EntityUtil
				.validFieldNamesWithCastedValues(requestedFilter, getEntityClass());

		// start with an empty list
		List<E> results = new ArrayList<>();

		List<Criterion> orPredicates = new ArrayList<>();

		if(origFieldNamesToCastedValues != null && !origFieldNamesToCastedValues.isEmpty()) {

			for (String fieldName : origFieldNamesToCastedValues.keySet()) {
				List<Object> fieldValues = origFieldNamesToCastedValues.get(fieldName);

				// if there are multiple values for a field name, we'll check
				// for equality and connect them with OR
				List<Criterion> eqExpressions = new ArrayList<>();

				for (Object fieldValue : fieldValues) {
					final SimpleExpression eq = Restrictions.eq(fieldName, fieldValue);
					eqExpressions.add(eq);
				}

				if(!eqExpressions.isEmpty()) {
					final Criterion[] eqArray = eqExpressions.toArray(new Criterion[0]);
					final Disjunction or = Restrictions.or(eqArray);
					orPredicates.add(or);
				}
			}

			if(!orPredicates.isEmpty()) {
				final Criterion[] orArray = orPredicates.toArray(new Criterion[0]);
				final Conjunction and = Restrictions.and(orArray);
				results = dao.findByCriteria(and);
			}
		}

		return results;
	}

	/**
	 *
	 * @param e
	 */
	@PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#e, 'DELETE')")
	public void delete(E e) {
		dao.delete(e);
	}

}
