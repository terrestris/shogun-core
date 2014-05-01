#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ${package}.dao.ProjectApplicationDao;
import ${package}.model.ProjectApplication;

@Service("projectApplicationService")
@Transactional(readOnly = true)
public class ProjectApplicationService {

	private static final Logger LOG = Logger
			.getLogger(ProjectApplicationService.class);

	@Autowired
	ProjectApplicationDao projectApplicationDao;

	@Transactional(readOnly = false)
	public ProjectApplication createProjectApplication(
			ProjectApplication application) {

		projectApplicationDao.saveOrUpdate(application);

		LOG.info("Saved ProjectApplication: " + application);

		return application;
	}

	public List<ProjectApplication> findAllProjectApplications() {
		LOG.info("finding all project apps");
		return projectApplicationDao.findAll();
	}

}
