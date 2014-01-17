package de.terrestris.shogun2.web;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.service.ApplicationService;

/**
 * @author Nils Buehner
 *
 */
@Controller
@RequestMapping("/application")
public class ApplicationController {

	private static final Logger log = Logger
			.getLogger(ApplicationController.class);

	@Autowired
	private ApplicationService applicationService;

	@RequestMapping(value = "/create.action", method = RequestMethod.GET)
	public @ResponseBody
	Application createApplication(String name, String description) {
		log.info("Requested to create a new Application.");

		Application application = new Application();
		application.setName(name);
		application.setDescription(description);
		application.setLanguage(Locale.getDefault());

		return applicationService.createApplication(application);
	}

	@RequestMapping(value = "/findAll.action", method = RequestMethod.GET)
	public @ResponseBody
	List<Application> findAllApplications() {
		log.info("Trying to find all Applications.");

		return applicationService.findAllApplications();
	}
}
