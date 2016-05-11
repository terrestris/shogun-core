package de.terrestris.shogun2.rest;

import java.io.Reader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.service.AbstractCrudService;
import de.terrestris.shogun2.web.AbstractWebController;

/**
 * @author Kai Volland
 * @author Daniel Koch
 * @author Nils Bühner
 *
 */
@RequestMapping(value = "/rest")
public abstract class AbstractRestController<E extends PersistentObject, D extends GenericHibernateDao<E, Integer>, S extends AbstractCrudService<E, D>>
		extends AbstractWebController<E, D, S> {

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected AbstractRestController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 *
	 */
	@Autowired
	protected ObjectMapper objectMapper;

	/**
	 * Find all entities.
	 *
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<E>> findAll() {
		final List<E> resultList = this.service.findAll();

		if (resultList != null && !resultList.isEmpty()) {
			LOG.trace("Found a total of " + resultList.size()
					+ " entities of type "
					+ resultList.get(0).getClass().getSimpleName());
		}

		return new ResponseEntity<List<E>>(resultList, HttpStatus.OK);
	}

	/**
	 * Get an entity by id.
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<E> findById(@PathVariable Integer id) {

		try {
			E entity = this.service.findById(id);
			LOG.trace("Found " + entity.getClass().getSimpleName()
					+ " with ID " + entity.getId());
			return new ResponseEntity<E>(entity, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Error finding entity with id " + id + ": "
					+ e.getMessage());
			return new ResponseEntity<E>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Create/save an entity.
	 *
	 * @param entity
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<E> save(@RequestBody E entity) {

		final String simpleClassName = entity.getClass().getSimpleName();
		final String errorMessagePrefix = "Error when saving entity of type "
				+ simpleClassName + ": ";

		// ID value MUST be null to assure that
		// saveOrUpdate will save and not update
		final Integer id = entity.getId();
		if (id != null) {
			LOG.error(errorMessagePrefix + "ID value is set to " + id
					+ ", but MUST be null");
			return new ResponseEntity<E>(HttpStatus.BAD_REQUEST);
		}

		try {
			this.service.saveOrUpdate(entity);
			LOG.trace("Created " + simpleClassName + " with ID " + entity.getId());
			return new ResponseEntity<E>(entity, HttpStatus.CREATED);
		} catch (Exception e) {
			LOG.error(errorMessagePrefix + e.getMessage());
			return new ResponseEntity<E>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Updates an entity by id.
	 *
	 * @param id
	 * @param entity
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<E> update(@PathVariable int id, HttpServletRequest request) {

		String errorPrefix = "Error updating "
				+ getEntityClass().getSimpleName() + " with ID " + id + ": ";

		Reader reader = null;

		try {
			// read and parse the json request body
			reader = request.getReader();
			JsonNode jsonObject = objectMapper.readTree(reader);

			// validate json object
			if(jsonObject == null || !jsonObject.has("id")) {
				LOG.error(errorPrefix
						+ "The JSON body is empty or has no 'id' property.");
				return new ResponseEntity<E>(HttpStatus.BAD_REQUEST);
			}

			// assure that the path variable id equals the payload id
			final int payloadId = jsonObject.get("id").asInt();
			if(payloadId != id) {
				LOG.error(errorPrefix + "Requested to update entity with ID "
						+ id + ", but payload ID is " + payloadId);
				return new ResponseEntity<E>(HttpStatus.BAD_REQUEST);
			}

			// get the persisted entity
			E entity = service.findById(id);

			if(entity != null){
				// we call this transactional method (instead of save or update)
				// to make sure that the possibly modified entity does not
				// get persisted / synced unexpectedly by hibernate
				// (due to FlushMode.AUTO) when another database-related
				// interaction is triggered in the meantime (which could happen
				// for example in a permission evaluation).
				// In other words: Do not get an entity, modify it and save it
				// in a non-transactional way (e.g. controller method), as
				// a possible permission evaluation could trigger an unwanted
				// persist action before the permission was evaluated.
				entity = service.updatePartialWithJsonNode(entity, jsonObject, objectMapper);
				return new ResponseEntity<E>(entity, HttpStatus.OK);
			}
			return new ResponseEntity<E>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			LOG.error(errorPrefix + e.getMessage());
			return new ResponseEntity<E>(HttpStatus.NOT_FOUND);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	/**
	 * Deletes an entity by id.
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<E> delete(@PathVariable int id) {

		try {
			E entityToDelete = this.service.findById(id);

			this.service.delete(entityToDelete);

			// extract the original classname from the name of the proxy, which
			// also contains _$$_ and some kind of hash after the original
			// classname
			final String proxyClassName = entityToDelete.getClass().getSimpleName();
			final String simpleClassName = StringUtils.substringBefore(proxyClassName, "_$$_");

			LOG.trace("Deleted " + simpleClassName + " with ID " + id);
			return new ResponseEntity<E>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			LOG.error("Error deleting entity with ID " + id + ": "
					+ e.getMessage());
			return new ResponseEntity<E>(HttpStatus.NOT_FOUND);
		}
	}

}
