#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ${package}.model.ProjectApplication;
import ${package}.service.ProjectApplicationService;
import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.web.ApplicationController;

@Controller
@RequestMapping("/projectApplication")
public class ProjectApplicationController {

	private static final Logger log = Logger
			.getLogger(ProjectApplicationController.class);

	@Autowired
	private ProjectApplicationService projectApplicationService;

	@RequestMapping(value = "/create.action", method = RequestMethod.GET)
	public @ResponseBody
	Application createApplication(String specificString, Integer specificInteger) {
		log.info("Trying to create a ProjectApplication now.");

		ProjectApplication application = new ProjectApplication();
		application.setName("Project App");
		application.setDescription("Proj App Desc");
		application.setLanguage(Locale.getDefault());

		application.setProjectSpecificString(specificString);
		application.setProjectSpecificInteger(specificInteger);

		return projectApplicationService.createProjectApplication(application);
	}

	@RequestMapping(value = "/findAll.action", method = RequestMethod.GET)
	public @ResponseBody
	List<ProjectApplication> findAllApplications() {
		log.info("Trying to find all ProjectApplications.");

		return projectApplicationService.findAllProjectApplications();
	}

}
