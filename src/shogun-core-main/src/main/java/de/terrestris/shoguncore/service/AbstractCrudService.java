package de.terrestris.shoguncore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shoguncore.dao.GenericHibernateDao;
import de.terrestris.shoguncore.model.PersistentObject;
import de.terrestris.shoguncore.util.entity.EntityUtil;
import org.hibernate.criterion.*;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This abstract service class provides basic CRUD functionality.
 *
 * @author Nils Bühner
 * @see AbstractDaoService
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
     * @param e
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
     */
    @PostAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(returnObject, 'READ')")
    @Transactional(readOnly = true)
    public E findById(Integer id) {
        return dao.findById(id);
    }

    /**
     * Return a proxy of the object (without hitting the database). This should
     * only be used if it is assumed that the object really exists and where
     * non-existence would be an actual error.
     *
     * @param id
     */
    @PostAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(returnObject, 'READ')")
    @Transactional(readOnly = true)
    public E loadById(int id) {
        return dao.loadById(id);
    }

    /**
     *
     */
    @PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<E> findAll() {
        return dao.findAll();
    }

    /**
     * Returns all entities, but possibly with only the passed fields set with actual values.
     */
    @PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(filterObject, 'READ')")
    public List<E> findAllRestricted(MultiValueMap<String, String> restrictToRequest) {
        List<String> restrictFields = EntityUtil.determineRestrictFields(restrictToRequest, getEntityClass());
        return dao.findByCriteriaRestricted(restrictFields);
    }

    /**
     * Finds all entities that match the given filter (multi value map).
     * <p>
     * Each key in the multi value map is the name of a field/property of the
     * entity. These values may be case insensitive! Each field name is mapped
     * to a list of values (passed as Strings).
     * <p>
     * Fields that do not exist in the entity will be ignored.
     * <p>
     * Example:
     * <p>
     * <pre>
     * int=['1','2']
     * string=['foo']
     * bool1=['0']
     * bool2=['true']
     * </pre>
     * <p>
     * would be translated to a filter like (values are casted to the target type of the field)
     * <p>
     * <pre>
     * (int == 1 || int == 2) &amp;&amp; (string == 'foo') &amp;&amp; (bool1 == false) &amp;&amp; (bool2 == true)
     * </pre>
     * <p>
     * and return all entities that match this condition.
     * <p>
     * This will only work on simple properties of an entity.
     * <p>
     * The optional special key {@value EntityUtil#RESTRICT_FIELDS_PARAM} can contain a
     * comma separated list of fieldNames you want to be filled with actual values. If you
     * e.g. pass <code>output:only=id,Name</code>, the returned object will have all other
     * fields being set to null, except for <code>id</code> and <code>name</code> (Notice
     * how casing does not matter).
     *
     * @param requestedFilter
     * @return
     */
    @PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<E> findBySimpleFilter(MultiValueMap<String, String> requestedFilter) {

        List<String> restrictFields = EntityUtil.determineRestrictFields(requestedFilter, getEntityClass());
        requestedFilter.remove(EntityUtil.RESTRICT_FIELDS_PARAM);

        MultiValueMap<String, Object> origFieldNamesToCastedValues = EntityUtil
            .validFieldNamesWithCastedValues(requestedFilter, getEntityClass());

        // start with an empty list
        List<E> results = new ArrayList<>();

        List<Criterion> orPredicates = new ArrayList<>();

        if (!origFieldNamesToCastedValues.isEmpty()) {
            for (Map.Entry<String, List<Object>> entry : origFieldNamesToCastedValues.entrySet()) {
                // if there are multiple values for a field name, we'll check
                // for equality and connect them with OR
                List<Criterion> eqExpressions = new ArrayList<>();
                List<Object> fieldValues = entry.getValue();

                for (Object fieldValue : fieldValues) {
                    final SimpleExpression eq = Restrictions.eq(entry.getKey(), fieldValue);
                    eqExpressions.add(eq);
                }

                if (!eqExpressions.isEmpty()) {
                    final Criterion[] eqArray = eqExpressions.toArray(new Criterion[0]);
                    final Disjunction or = Restrictions.or(eqArray);
                    orPredicates.add(or);
                }
            }

            if (!orPredicates.isEmpty()) {
                final Criterion[] orArray = orPredicates.toArray(new Criterion[0]);
                final Conjunction and = Restrictions.and(orArray);
                results = dao.findByCriteriaRestricted(restrictFields, and);
            }
        }

        return results;
    }

    /**
     * Returns a list of entity objects that have field named
     * <code>fieldName</code>, which has an object <code>fieldEntity</code>
     * as value.
     *
     * @param fieldName  The name of the field
     * @param fieldValue The element that should be set as value
     * @return The list of objects
     */
    @PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<E> findAllWhereFieldEquals(String fieldName, Object fieldValue) {
        return dao.findAllWhereFieldEquals(fieldName, fieldValue);
    }

    /**
     * Returns a list of entity objects that have a collection named
     * <code>fieldName</code>, which contains the passed
     * <code>subElement</code>.
     * <p>
     * The can e.g. be used to return all applications that contain a certain layer.
     *
     * @param fieldName  The name of the collection field
     * @param subElement The element that should be contained in the collection
     * @return The list of objects
     */
    @PostFilter("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(filterObject, 'READ')")
    @Transactional(readOnly = true)
    public List<E> findAllWithCollectionContaining(String fieldName, PersistentObject subElement) {
        return dao.findAllWithCollectionContaining(fieldName, subElement);
    }

    /**
     * @param e
     */
    @PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#e, 'DELETE')")
    public void delete(E e) {
        dao.delete(e);
    }

}
