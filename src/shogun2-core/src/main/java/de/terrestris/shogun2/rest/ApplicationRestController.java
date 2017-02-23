package de.terrestris.shogun2.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.ApplicationDao;
import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.service.ApplicationService;

/**
 * @author Kai Volland
 * @author Nils Bühner
 *
 */
@RestController
@RequestMapping("/applications")
public class ApplicationRestController<E extends Application, D extends ApplicationDao<E>, S extends ApplicationService<E, D>>
		extends AbstractRestController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public ApplicationRestController() {
		this((Class<E>) Application.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected ApplicationRestController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("applicationService")
	public void setService(S service) {
		this.service = service;
	}

	/**
	 * Find all template applications.
	 *
	 * @return
	 */
	@RequestMapping(value = "/templates", method = RequestMethod.GET)
	public ResponseEntity<List<E>> findAllTemplates() {
		final List<E> resultList = this.service.findAllTemplates();

		if (resultList != null && !resultList.isEmpty()) {
			LOG.trace("Found a total of " + resultList.size()
					+ " template applications.");
		}

		return new ResponseEntity<List<E>>(resultList, HttpStatus.OK);
	}

}
