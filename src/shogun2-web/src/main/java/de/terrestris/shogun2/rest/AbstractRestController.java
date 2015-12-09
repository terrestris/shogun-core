package de.terrestris.shogun2.rest;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
	public List<E> findAll() {
		return this.service.findAll();
	}

	/**
	 * Create an entity.
	 * @param entity
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public E create(@RequestBody E entity) {
		LOG.debug("create() with body "+entity+" of type " + entity.getClass());
		E created = this.service.saveOrUpdate(entity);
		return created;
	}

	/**
	 * Get an entity by id.
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public E get(@PathVariable int id) {
		E entity = this.service.findById(id);
		return entity;
	}

	/**
	 * Updates an entity by id.
	 * @param id
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.POST)
	public E update(@PathVariable int id, @RequestBody E entity) throws Exception {
		LOG.debug("update " + entity.getClass() + id +" with body" + entity);

		if (entity.getId() == id) {
			E updated = this.service.saveOrUpdate(entity);
			LOG.debug("updated entity: " + updated);
			return updated;
		} else {
			throw new IllegalArgumentException(
					"The entities id is not equal to the id in the path.");
		}
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public E delete(@PathVariable int id) {
		E entity = this.service.findById(id);
		this.service.delete(entity);
		return entity;
	}

}
