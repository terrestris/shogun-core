package de.terrestris.shogun2.service;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.dao.PermissionCollectionDao;
import de.terrestris.shogun2.model.SecuredPersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.model.security.Permission;
import de.terrestris.shogun2.model.security.PermissionCollection;

/**
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
public abstract class AbstractSecuredPersistentObjectService<E extends SecuredPersistentObject, D extends GenericHibernateDao<E, Integer>>
		extends AbstractCrudService<E, D> {

	@Autowired
	@Qualifier("permissionCollectionService")
	protected PermissionCollectionService<PermissionCollection, PermissionCollectionDao<PermissionCollection>> permissionCollectionService;

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public AbstractSecuredPersistentObjectService() {
		this((Class<E>) SecuredPersistentObject.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected AbstractSecuredPersistentObjectService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * This method adds (user) permissions to the passed entity and persists (!)
	 * the permission collection!
	 * If no permissions have been set before, they will be created. Otherwise
	 * the passed permissions will be added to the existing permission
	 * collection.
	 *
	 * @param entity The secured entity
	 * @param user The user that gets permissions for the entity
	 * @param permissions The permissions the user gets for the entity
	 */
	public void addUserPermissions(E entity, User user, Permission... permissions) {
		if(entity != null) {
			// create a set from the passed array
			final HashSet<Permission> permissionsSet = new HashSet<Permission>(Arrays.asList(permissions));

			if(permissionsSet != null && !permissionsSet.isEmpty()) {
				// get the existing permission
				PermissionCollection userPermissionCollection = entity.getUserPermissions().get(user);

				// whether or not we have to persist the entity (which is only the case
				// if a new permission collection will be created in the next step)
				boolean persistEntity = false;

				if(userPermissionCollection == null) {
					// create a new user permission collection and attach it to the user
					entity.getUserPermissions().put(user, new PermissionCollection(permissionsSet));
					LOG.debug("Attached a new permission collection for a user: " + permissionsSet);

					// also persist the entity as a new permission collection has been attached
					persistEntity = true;
				} else {
					// add the passed permissions to the the existing permission collection
					userPermissionCollection.getPermissions().addAll(permissionsSet);
					LOG.debug("Added the following permissions to an existing permission collection: "
							+ permissionsSet);
				}

				// persist the permission collection
				permissionCollectionService.saveOrUpdate(userPermissionCollection);
				LOG.debug("Persisted a permission collection");

				// persist the entity if necessary
				if(persistEntity) {
					this.saveOrUpdate(entity);
					LOG.debug("Persisted the entity with a new permission collection.");
				}
			} else {
				LOG.error("Could not add permissions: No permissions have been passed." );
			}
		} else {
			LOG.error("Could not add permissions: The passed entity is NULL.");
		}
	}

	/**
	 * This method removes (user) permissions from the passed entity and persists (!)
	 * the permission collection!
	 *
	 * @param entity The secured entity
	 * @param user The user from which the permissions for the entity will be removed
	 * @param permissions The permissions to remove
	 */
	public void removeUserPermissions(E entity, User user, Permission... permissions) {
		if(entity != null) {
			// create a set from the passed array
			final HashSet<Permission> permissionsSet = new HashSet<Permission>(Arrays.asList(permissions));

			if(permissionsSet != null && !permissionsSet.isEmpty()) {
				// get the existing permission
				PermissionCollection userPermissionCollection = entity.getUserPermissions().get(user);

				if(userPermissionCollection != null) {
					// remove the passed permissions from the the existing permission collection
					userPermissionCollection.getPermissions().removeAll(permissionsSet);
					LOG.debug("Removed the following permissions from an existing permission collection: "
							+ permissionsSet);
					// persist the permission collection
					permissionCollectionService.saveOrUpdate(userPermissionCollection);
					LOG.debug("Persisted a permission collection");
				} else {
					LOG.warn("Could not remove permissions as there is no attached permission collection.");
				}
			} else {
				LOG.error("Could not remove permissions: No permissions have been passed." );
			}
		} else {
			LOG.error("Could not remove permissions: The passed entity is NULL.");
		}
	}

	/**
	 * This method adds (user) permissions to the passed entity and persists (!)
	 * the permission collection!
	 * If no permissions have been set before, they will be created. Otherwise
	 * the passed permissions will be added to the existing permission
	 * collection.
	 *
	 * @param entity The secured entity
	 * @param userGroup The user group that gets permissions for the entity
	 * @param permissions The permissions the user group gets for the entity
	 */
	public void addGroupPermissions(E entity, UserGroup userGroup, Permission... permissions) {
		if(entity != null) {
			// create a set from the passed array
			final HashSet<Permission> permissionsSet = new HashSet<Permission>(Arrays.asList(permissions));

			if(permissionsSet != null && !permissionsSet.isEmpty()) {
				// get the existing permission
				PermissionCollection groupPermissionCollection = entity.getGroupPermissions().get(userGroup);

				// whether or not we have to persist the entity (which is only the case
				// if a new permission collection will be created in the next step)
				boolean persistEntity = false;

				if(groupPermissionCollection == null) {
					// create a new user permission collection and attach it to the user
					entity.getGroupPermissions().put(userGroup, new PermissionCollection(permissionsSet));
					LOG.debug("Attached a new permission collection for a group: " + permissionsSet);

					// also persist the entity as a new permission collection has been attached
					persistEntity = true;
				} else {
					// add the passed permissions to the the existing permission collection
					groupPermissionCollection.getPermissions().addAll(permissionsSet);
					LOG.debug("Added the following permissions to an existing permission collection: "
							+ permissionsSet);
				}

				// persist the permission collection
				permissionCollectionService.saveOrUpdate(groupPermissionCollection);
				LOG.debug("Persisted a permission collection");

				// persist the entity if necessary
				if(persistEntity) {
					this.saveOrUpdate(entity);
					LOG.debug("Persisted the entity with a new permission collection.");
				}
			} else {
				LOG.error("Could not add permissions: No permissions have been passed." );
			}
		} else {
			LOG.error("Could not add permissions: The passed entity is NULL.");
		}
	}

	/**
	 * This method removes (group) permissions from the passed entity and persists (!)
	 * the permission collection!
	 *
	 * @param entity The secured entity
	 * @param userGroup The user group from which the permissions for the entity will be removed
	 * @param permissions The permissions to remove
	 */
	public void removeGroupPermissions(E entity, UserGroup userGroup, Permission... permissions) {
		if(entity != null) {
			// create a set from the passed array
			final HashSet<Permission> permissionsSet = new HashSet<Permission>(Arrays.asList(permissions));

			if(permissionsSet != null && !permissionsSet.isEmpty()) {
				// get the existing permission
				PermissionCollection groupPermissionCollection = entity.getGroupPermissions().get(userGroup);

				if(groupPermissionCollection != null) {
					// remove the passed permissions from the the existing permission collection
					groupPermissionCollection.getPermissions().removeAll(permissionsSet);
					LOG.debug("Removed the following permissions from an existing permission collection: "
							+ permissionsSet);
					// persist the permission collection
					permissionCollectionService.saveOrUpdate(groupPermissionCollection);
					LOG.debug("Persisted a permission collection");
				} else {
					LOG.error("Could not remove permissions as there is no attached permission collection.");
				}
			} else {
				LOG.error("Could not remove permissions: No permissions have been passed." );
			}
		} else {
			LOG.error("Could not remove permissions: The passed entity is NULL.");
		}
	}

	/**
	 * @return the permissionCollectionService
	 */
	public PermissionCollectionService<PermissionCollection, PermissionCollectionDao<PermissionCollection>> getPermissionCollectionService() {
		return permissionCollectionService;
	}

	/**
	 * @param permissionCollectionService the permissionCollectionService to set
	 */
	public void setPermissionCollectionService(
			PermissionCollectionService<PermissionCollection, PermissionCollectionDao<PermissionCollection>> permissionCollectionService) {
		this.permissionCollectionService = permissionCollectionService;
	}

}
