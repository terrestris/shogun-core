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
@RequestMapping(value="/rest")
public abstract class AbstractRestController<E extends PersistentObject> {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger.getLogger(AbstractRestController.class);

	@Autowired
	private AbstractCrudService<E> service;

	/**
	 * Find all Entities.
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<E>> findAll() {
		return new ResponseEntity<List<E>>(this.service.findAll(), HttpStatus.OK);
	}

	/**
	 * Create an entity.
	 * @param entity
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<E> create(@RequestBody E entity) {
		LOG.debug("create() with body "+entity+" of type " + entity.getClass());
		E created = this.service.saveOrUpdate(entity);
		return new ResponseEntity<E>(created, HttpStatus.OK);
	}

	/**
	 * Get an entity by id.
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<E> get(@PathVariable int id) {
		E entity = this.service.findById(id);
		return new ResponseEntity<E>(entity, HttpStatus.OK);
	}

	/**
	 * Updates an entity by id.
	 * @param id
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<E> update(@PathVariable int id, @RequestBody E entity) {
		LOG.debug("update " + entity.getClass() + id +" with body" + entity);

		if (entity.getId() == id) {
			E updated = this.service.saveOrUpdate(entity);
			LOG.debug("updated entity: " + updated);
			return new ResponseEntity<E>(updated, HttpStatus.OK);
		} else {
			return new ResponseEntity<E>(HttpStatus.NOT_MODIFIED);
		}
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<E> delete(@PathVariable int id) {
		E deleted = this.service.findById(id);
		this.service.delete(deleted);
		return new ResponseEntity<E>(deleted, HttpStatus.OK);
	}

}
