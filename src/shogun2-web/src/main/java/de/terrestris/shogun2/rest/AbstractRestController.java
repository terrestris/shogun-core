package de.terrestris.shogun2.rest;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
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
	 * @param json
	 * @return
	 */
	@RequestMapping(method=RequestMethod.POST)
	public E create(@RequestBody E json) {
		LOG.info("create() with body "+json+" of type " + json.getClass());
		E created = this.service.saveOrUpdate(json);
		return created;
	}

	/**
	 * Get an entity by id.
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public E get(@PathVariable int id) {
		E instance = this.service.findById(id);
		return instance;
	}

	/**
	 * Updates an entity by id.
	 * @param id
	 * @param json
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/{id}", method=RequestMethod.POST)
	public E update(@PathVariable int id, @RequestBody E json) throws Exception {
		LOG.info("update() of id#"+id+" with body "+json);
		LOG.info("E json is of type "+json.getClass());

		E entity = this.service.findById(id);
		try {
			BeanUtils.copyProperties(entity, json);
		}
		catch (Exception e) {
			LOG.info("while copying properties " + e);
			throw e;
		}

		LOG.info("merged entity: " + entity);

		E updated = this.service.saveOrUpdate(entity);
		LOG.info("updated enitity: " + updated);

		return updated;
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
