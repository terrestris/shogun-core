package de.terrestris.shogun2.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.terrestris.shogun2.model.Module;
import de.terrestris.shogun2.service.ModuleService;

/**
 * @author Nils Bühner
 * 
 */
@Controller
@RequestMapping("/module")
public class ModuleController {

	private static final Logger LOG = Logger.getLogger(ModuleController.class);

	@Autowired
	private ModuleService moduleService;

	@RequestMapping(value = "/findAll.action", method = RequestMethod.GET)
	public @ResponseBody List<Module> findAllModules() {
		LOG.info("Trying to find all modules.");

		return moduleService.findAll();
	}
}
