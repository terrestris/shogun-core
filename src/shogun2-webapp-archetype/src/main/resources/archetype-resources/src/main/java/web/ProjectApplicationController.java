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

@Controller
@RequestMapping("/projectApplication")
public class ProjectApplicationController {

	private static final Logger LOG = Logger
			.getLogger(ProjectApplicationController.class);

	@Autowired
	private ProjectApplicationService projectApplicationService;

	@RequestMapping(value = "/create.action", method = RequestMethod.GET)
	public @ResponseBody
	ProjectApplication createApplication(String specificString, Integer specificInteger) {
		LOG.info("Trying to create a ProjectApplication now.");

		ProjectApplication application = new ProjectApplication();
		application.setName("Project App");
		application.setDescription("Proj App Desc");
		application.setLanguage(Locale.getDefault());

		application.setProjectSpecificString(specificString);
		application.setProjectSpecificInteger(specificInteger);

		return projectApplicationService.saveOrUpdate(application);
	}

	@RequestMapping(value = "/delete.action", method = RequestMethod.GET)
	public void deleteApplication(Integer id) {
		LOG.info("Trying to delete ProjectApplication " + id + " now.");

		ProjectApplication app = projectApplicationService.findById(id);
		projectApplicationService.delete(app);

		LOG.info("Deleted project app " + id);
	}

	@RequestMapping(value = "/findAll.action", method = RequestMethod.GET)
	public @ResponseBody
	List<ProjectApplication> findAllApplications() {
		LOG.info("Trying to find all ProjectApplications.");

		return projectApplicationService.findAll();
	}

}
