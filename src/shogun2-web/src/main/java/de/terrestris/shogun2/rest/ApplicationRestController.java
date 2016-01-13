package de.terrestris.shogun2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
