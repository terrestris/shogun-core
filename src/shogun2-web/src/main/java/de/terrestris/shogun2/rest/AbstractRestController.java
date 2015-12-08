package de.terrestris.shogun2.rest;

import java.util.List;

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

//	private Logger logger = LoggerFactory.getLogger(RESTController.class);

	@Autowired
	private AbstractCrudService<E> service;

//	public AbstractRestController(AbstractCrudService<E> service) {
//		this.service = service;
//	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public List<E> findAll() {
		return this.service.findAll();
	}

	// , consumes={MediaType.APPLICATION_JSON_VALUE}
	@RequestMapping(method=RequestMethod.POST)
	public E create(@RequestBody E json) {
//		logger.debug("create() with body {} of type {}", json, json.getClass());

//		ObjectMapper mapper = new ObjectMapper();
//		//JSON from String to Object
//		User user = mapper.readValue(json, E);

		E created = this.service.saveOrUpdate(json);

		return created;
	}

	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public E get(@PathVariable int id) {
		E instance = this.service.findById(id);
//		if (instance == null) {
//			System.out.println(instance + " with id " + id + " not found");
//			return HttpStatus.NOT_FOUND;
//		}
		return instance;
	}
//
//	@RequestMapping(value="/{id}", method=RequestMethod.POST, consumes={MediaType.APPLICATION_JSON_VALUE})
//	public @ResponseBody Map<String, Object> update(@PathVariable ID id, @RequestBody T json) {
//		logger.debug("update() of id#{} with body {}", id, json);
//		logger.debug("T json is of type {}", json.getClass());
//
//		T entity = this.repo.findOne(id);
//		try {
//			BeanUtils.copyProperties(entity, json);
//		}
//		catch (Exception e) {
//			logger.warn("while copying properties", e);
//			throw Throwables.propagate(e);
//		}
//
//		logger.debug("merged entity: {}", entity);
//
//		T updated = this.repo.save(entity);
//		logger.debug("updated enitity: {}", updated);
//
//		Map<String, Object> m = Maps.newHashMap();
//		m.put("success", true);
//		m.put("id", id);
//		m.put("updated", updated);
//		return m;
//	}
//
//	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
//	public @ResponseBody Map<String, Object> delete(@PathVariable ID id) {
//		this.repo.delete(id);
//		Map<String, Object> m = Maps.newHashMap();
//		m.put("success", true);
//		return m;
//	}

}
