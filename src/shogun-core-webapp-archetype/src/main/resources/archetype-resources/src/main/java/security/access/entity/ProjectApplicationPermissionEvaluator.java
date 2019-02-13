#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.security.access.entity;

import ${package}.model.ProjectApplication;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.security.Permission;
import de.terrestris.shogun2.security.access.entity.PersistentObjectPermissionEvaluator;

/**
 * Demonstrates the implementation of a custom permission evaluator for
 * {@link ProjectApplication}.
 * 
 * @author Nils Bühner
 *
 */
public class ProjectApplicationPermissionEvaluator<E extends ProjectApplication> extends
		PersistentObjectPermissionEvaluator<E> {

	/**
	 * Default constructor
	 */
	@SuppressWarnings("unchecked")
	public ProjectApplicationPermissionEvaluator() {
		this((Class<E>) ProjectApplication.class);
	}

	/**
	 * Constructor for subclasses
	 *
	 * @param entityClass
	 */
	protected ProjectApplicationPermissionEvaluator(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * Always grants right to CREATE this entity.
	 */
	@Override
	public boolean hasPermission(User user, E entity, Permission permission) {

		// always grant CREATE right for this entity
		// This is just a demo!
		if (permission.equals(Permission.CREATE)) {
			LOG.trace("Granting CREATE for project application.");
			return true;
		}

		// call parent implementation from SHOGun-Core
		return super.hasPermission(user, entity, permission);
	}

}
