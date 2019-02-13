#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.security.access.factory;

import ${package}.model.ProjectApplication;
{package}.security.access.entity.ProjectApplicationPermissionEvaluator;
import de.terrestris.shoguncore.model.PersistentObject;
import de.terrestris.shoguncore.security.access.entity.PersistentObjectPermissionEvaluator;
import de.terrestris.shoguncore.security.access.factory.EntityPermissionEvaluatorFactory;


/**
 * This is just a demo to show how the {@link EntityPermissionEvaluatorFactory}
 * from SHOGun-Core can be extended to make use of it in a project specific
 * implementation.
 * 
 * This class has to be configured to be used for the permissionEvaluator (of
 * SHOGun-Core) in the security XML of this project.
 * 
 * @author Nils Bühner
 *
 */
public class ProjectEntityPermissionEvaluatorFactory<E extends PersistentObject> extends EntityPermissionEvaluatorFactory<E> {

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PersistentObjectPermissionEvaluator<E> getEntityPermissionEvaluator(
			final Class<E> entityClass) {

		if(ProjectApplication.class.isAssignableFrom(entityClass)) {
			return new ProjectApplicationPermissionEvaluator();
		}

		// call SHOGun-Core implementation otherwise
		return super.getEntityPermissionEvaluator(entityClass);

	}

}
