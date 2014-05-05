#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import org.springframework.stereotype.Service;
import de.terrestris.shogun2.service.AbstractCrudService;

import ${package}.model.ProjectApplication;

@Service("projectApplicationService")
public class ProjectApplicationService extends
		AbstractCrudService<ProjectApplication> {
}
