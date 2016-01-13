package de.terrestris.shogun2.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.terrestris.shogun2.dao.ModuleDao;
import de.terrestris.shogun2.model.module.Module;
import de.terrestris.shogun2.service.ModuleService;

/**
 * @author Nils Bühner
 *
 */
@Controller
@RequestMapping("/module")
public class ModuleController<E extends Module, D extends ModuleDao<E>, S extends ModuleService<E, D>>
		extends AbstractWebController<E, D, S> {

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("moduleService")
	public void setService(S service) {
		this.service = service;
	}
}
