package de.terrestris.shogun2.security.access.factory;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.Role;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.model.interceptor.InterceptorRule;
import de.terrestris.shogun2.model.layer.appearance.LayerAppearance;
import de.terrestris.shogun2.model.layer.source.LayerDataSource;
import de.terrestris.shogun2.model.layer.util.Extent;
import de.terrestris.shogun2.model.layer.util.TileGrid;
import de.terrestris.shogun2.model.layout.Layout;
import de.terrestris.shogun2.model.map.MapConfig;
import de.terrestris.shogun2.model.map.MapControl;
import de.terrestris.shogun2.model.module.Module;
import de.terrestris.shogun2.model.security.PermissionCollection;
import de.terrestris.shogun2.model.token.Token;
import de.terrestris.shogun2.security.access.entity.AlwaysAllowReadPermissionEvaluator;
import de.terrestris.shogun2.security.access.entity.PermissionCollectionPermissionEvaluator;
import de.terrestris.shogun2.security.access.entity.PersistentObjectPermissionEvaluator;
import de.terrestris.shogun2.security.access.entity.UserGroupPermissionEvaluator;
import de.terrestris.shogun2.security.access.entity.UserPermissionEvaluator;


/**
 * @author Nils Bühner
 *
 */
public class EntityPermissionEvaluatorFactory<E extends PersistentObject> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PersistentObjectPermissionEvaluator<E> getEntityPermissionEvaluator(
			final Class<E> entityClass) {

		if(PermissionCollection.class.isAssignableFrom(entityClass)) {
			return new PermissionCollectionPermissionEvaluator();
		}

		if(User.class.isAssignableFrom(entityClass)) {
			return new UserPermissionEvaluator();
		}

		if(UserGroup.class.isAssignableFrom(entityClass)) {
			return new UserGroupPermissionEvaluator();
		}

		// The following types (and subclasses) may be READ by everyone
		// by default. If a type is not listed here, explicit Permissions
		// have to be set for the entities of these types.
		//
		// NOT listed here (and therefore "fully secured") are the following
		// classes AND (!) their subclasses:
		//
		// * Layer
		// * Application
		// * File
		// * Person
		// * UserGroup
		if(Extent.class.isAssignableFrom(entityClass) ||
			InterceptorRule.class.isAssignableFrom(entityClass) ||
			LayerAppearance.class.isAssignableFrom(entityClass) ||
			LayerDataSource.class.isAssignableFrom(entityClass) ||
			Layout.class.isAssignableFrom(entityClass) ||
			MapConfig.class.isAssignableFrom(entityClass) ||
			MapControl.class.isAssignableFrom(entityClass) ||
			Module.class.isAssignableFrom(entityClass) ||
			Role.class.isAssignableFrom(entityClass) ||
			TileGrid.class.isAssignableFrom(entityClass) ||
			Token.class.isAssignableFrom(entityClass)) {

			// always grants READ permission (but no other permission)
			// project specific requirements require implementations
			// of custom permission evaluators
			return new AlwaysAllowReadPermissionEvaluator();
		}

		// fall back on default implementation
		return new PersistentObjectPermissionEvaluator<E>(entityClass);

	}

}
