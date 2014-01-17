#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao;

import org.springframework.stereotype.Repository;

import ${package}.model.ProjectApplication;
import de.terrestris.shogun2.dao.GenericHibernateDao;

@Repository
public class ProjectApplicationDao extends
		GenericHibernateDao<ProjectApplication, Integer> {

	protected ProjectApplicationDao() {
		super(ProjectApplication.class);
	}

}
