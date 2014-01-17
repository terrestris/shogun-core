#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.init;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import ${package}.model.ProjectApplication;
import ${package}.service.ProjectApplicationService;
import de.terrestris.shogun2.init.ContentInitializer;

public class ProjectContentInitializer extends ContentInitializer {

	/**
	 * The Logger
	 */
	private static Logger log = Logger.getLogger(ProjectContentInitializer.class);

	/**
	 * Flag symbolizing if something should be initialized on startup
	 */
	@Autowired
	@Qualifier("projectInitEnabled")
	private Boolean projectInitEnabled;

	/**
	 * Number of dummy-entries to create when shogunInitEnabled=true
	 */
	@Autowired
	@Qualifier("numberOfProjectDummies")
	private int numberOfProjectDummies;

	@Autowired
	private ProjectApplicationService projectApplicationService;

	@Override
	public void initializeDatabaseContent() {

		super.initializeDatabaseContent();

		if (this.projectInitEnabled.equals(true)) {
			log.info("Initializing " + numberOfProjectDummies
					+ " project application dummies.");

			Set<ProjectApplication> createdApps = new HashSet<ProjectApplication>();

			for (int i = 0; i < numberOfProjectDummies; i++) {
				String name = "ProjectApplication " + i;
				String desc = "Description of ProjectApplication " + i;

				ProjectApplication application = new ProjectApplication();
				application.setName(name);
				application.setDescription(desc);
				application.setLanguage(Locale.getDefault());
				application.setProjectSpecificInteger(17);
				application.setProjectSpecificString("seventeen");

				application = projectApplicationService.createProjectApplication(application);

				createdApps.add(application);
			}

			log.info("Created " + createdApps.size() + " applications for the project.");
		} else {
			log.info("Not initializing anything for Project. Searching for existing apps now.");

			List<ProjectApplication> existingApps = projectApplicationService
					.findAllProjectApplications();

			log.info("Found " + existingApps.size()
					+ " existing applications.");
		}

	}

}
