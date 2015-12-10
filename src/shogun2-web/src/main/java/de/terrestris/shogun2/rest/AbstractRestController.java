package de.terrestris.shogun2.rest;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.service.AbstractCrudService;

/**
 * @author Kai Volland
 * @author Daniel Koch
 *
 */
@RequestMapping(value = "/rest")
public abstract class AbstractRestController<E extends PersistentObject> {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger
			.getLogger(AbstractRestController.class);

	@Autowired
	private AbstractCrudService<E> service;

	/**
	 * Find all Entities.
	 *
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<E>> findAll() {
		return new ResponseEntity<List<E>>(this.service.findAll(),
				HttpStatus.OK);
	}

	/**
	 * Get an entity by id.
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<E> get(@PathVariable int id) {
		E entity = this.service.findById(id);
		return new ResponseEntity<E>(entity, HttpStatus.OK);
	}

	/**
	 * Create an entity.
	 *
	 * @param entity
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<E> create(@RequestBody E entity) {
		try {
			E created = this.service.saveOrUpdate(entity);
			LOG.debug("Create " + entity.getClass() + " with body " + entity);
			return new ResponseEntity<E>(created, HttpStatus.CREATED);
		} catch (Exception e) {
			LOG.debug("Malformed body: " + entity);
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
	public ResponseEntity<E> update(@PathVariable int id, @RequestBody E entity) {
		if (entity.getId() == id) {
			try {
				E updated = this.service.saveOrUpdate(entity);
				LOG.debug("Updated entity: " + updated);
				return new ResponseEntity<E>(updated, HttpStatus.OK);
			} catch (Exception e) {
				LOG.error("There is no " + entity.getClass() + " with id " + id);
				return new ResponseEntity<E>(HttpStatus.NOT_FOUND);
			}
		} else {
			LOG.error("Can't update " + id + " with body" + entity);
			return new ResponseEntity<E>(HttpStatus.BAD_REQUEST);
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
		E deleted = this.service.findById(id);
		if(deleted != null) {
			this.service.delete(deleted);
			LOG.debug("Delted " + id + " of type " + deleted.getClass());
			return new ResponseEntity<E>(deleted, HttpStatus.OK);
		} else {
			LOG.error("There is no object with id " + id);
			return new ResponseEntity<E>(HttpStatus.NOT_FOUND);
		}
	}

}
