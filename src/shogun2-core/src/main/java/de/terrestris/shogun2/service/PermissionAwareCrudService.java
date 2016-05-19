package de.terrestris.shogun2.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.dao.PermissionCollectionDao;
import de.terrestris.shogun2.model.PersistentObject;
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
@Service("permissionAwareCrudService")
public class PermissionAwareCrudService<E extends PersistentObject, D extends GenericHibernateDao<E, Integer>>
		extends AbstractCrudService<E, D> {

	/**
	 *
	 */
	@Autowired
	@Qualifier("permissionCollectionService")
	protected PermissionCollectionService<PermissionCollection, PermissionCollectionDao<PermissionCollection>> permissionCollectionService;


	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public PermissionAwareCrudService() {
		this((Class<E>) PersistentObject.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected PermissionAwareCrudService(Class<E> entityClass) {
		super(entityClass);
	}

	@Override
	@Autowired
	@Qualifier("genericDao")
	public void setDao(D dao) {
		this.dao = dao;
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
	public void addAndSaveUserPermissions(E entity, User user, Permission... permissions) {
		if(entity == null) {
			LOG.error("Could not add permissions: The passed entity is NULL.");
			return;
		}

		// create a set from the passed array
		final HashSet<Permission> permissionsSet = new HashSet<Permission>(Arrays.asList(permissions));

		if(permissionsSet == null || permissionsSet.isEmpty()) {
			LOG.error("Could not add permissions: No permissions have been passed." );
			return;
		}

		// get the existing permission
		PermissionCollection userPermissionCollection = entity.getUserPermissions().get(user);

		// whether or not we have to persist the permission collection (which is only
		// the case if it is new or its size has changed)
		boolean persistPermissionCollection = false;

		// whether or not we have to persist the entity (which is only the case
		// if a new permission collection will be created in the next step)
		boolean persistEntity = false;

		if(userPermissionCollection == null) {
			// create a new user permission collection and attach it to the user
			userPermissionCollection = new PermissionCollection(permissionsSet);
			entity.getUserPermissions().put(user, userPermissionCollection);
			LOG.debug("Attached a new permission collection for a user: " + permissionsSet);

			// persist permission collection and the entity as a new permission
			// collection has been attached
			persistPermissionCollection = true;
			persistEntity = true;
		} else {
			Set<Permission> userPermissions = userPermissionCollection.getPermissions();
			int originalNrOfPermissions = userPermissions.size();

			// add the passed permissions to the the existing permission collection
			userPermissions.addAll(permissionsSet);

			int newNrOfPermissions = userPermissions.size();

			if(newNrOfPermissions > originalNrOfPermissions) {
				// persist the collection as we have "really" added new permission(s)
				persistPermissionCollection = true;
				LOG.debug("Added the following permissions to an existing permission collection: "
						+ permissionsSet);
			}
		}

		if(persistPermissionCollection) {
			// persist the permission collection
			permissionCollectionService.saveOrUpdate(userPermissionCollection);
			LOG.debug("Persisted a permission collection");

			// persist the entity if necessary
			if(persistEntity) {
				this.saveOrUpdate(entity);
				LOG.debug("Persisted the entity with a new permission collection.");
			}
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
	public void removeAndSaveUserPermissions(E entity, User user, Permission... permissions) {
		if(entity == null) {
			LOG.error("Could not remove permissions: The passed entity is NULL.");
			return;
		}

		// create a set from the passed array
		final HashSet<Permission> permissionsSet = new HashSet<Permission>(Arrays.asList(permissions));

		if(permissionsSet == null || permissionsSet.isEmpty()) {
			LOG.error("Could not remove permissions: No permissions have been passed." );
			return;
		}

		// get the existing permission
		PermissionCollection userPermissionCollection = entity.getUserPermissions().get(user);

		if(userPermissionCollection == null) {
			LOG.error("Could not remove permissions as there is no attached permission collection.");
			return;
		}

		Set<Permission> userPermissions = userPermissionCollection.getPermissions();

		int originalNrOfPermissions = userPermissions.size();

		// remove the passed permissions from the the existing permission collection
		userPermissions.removeAll(permissionsSet);

		int newNrOfPermissions = userPermissions.size();

		if(newNrOfPermissions == 0) {
			LOG.debug("The permission collection is empty and will thereby be deleted now.");

			// remove
			entity.getUserPermissions().remove(user);
			this.saveOrUpdate(entity);

			permissionCollectionService.delete(userPermissionCollection);

			return;
		}

		// only persist if we have "really" removed something
		if(newNrOfPermissions < originalNrOfPermissions) {
			LOG.debug("Removed the following permissions from an existing permission collection: "
					+ permissionsSet);
			// persist the permission collection
			permissionCollectionService.saveOrUpdate(userPermissionCollection);
			LOG.debug("Persisted a permission collection");
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
	public void addAndSaveGroupPermissions(E entity, UserGroup userGroup, Permission... permissions) {
		if(entity == null) {
			LOG.error("Could not add permissions: The passed entity is NULL.");
			return;
		}

		// create a set from the passed array
		final HashSet<Permission> permissionsSet = new HashSet<Permission>(Arrays.asList(permissions));

		if(permissionsSet == null || permissionsSet.isEmpty()) {
			LOG.error("Could not add permissions: No permissions have been passed." );
			return;
		}

		// get the existing permission
		PermissionCollection groupPermissionCollection = entity.getGroupPermissions().get(userGroup);

		// whether or not we have to persist the permission collection (which is only
		// the case if it is new or its size has changed)
		boolean persistPermissionCollection = false;

		// whether or not we have to persist the entity (which is only the case
		// if a new permission collection will be created in the next step)
		boolean persistEntity = false;

		if(groupPermissionCollection == null) {
			// create a new user permission collection and attach it to the user
			groupPermissionCollection = new PermissionCollection(permissionsSet);

			entity.getGroupPermissions().put(userGroup, groupPermissionCollection);
			LOG.debug("Attached a new permission collection for a group: " + permissionsSet);

			// persist permission collection and the entity as a new permission
			// collection has been attached
			persistPermissionCollection = true;
			persistEntity = true;
		} else {
			Set<Permission> groupPermissions = groupPermissionCollection.getPermissions();
			int originalNrOfPermissions = groupPermissions.size();

			// add the passed permissions to the the existing permission collection
			groupPermissions.addAll(permissionsSet);

			int newNrOfPermissions = groupPermissions.size();

			if(newNrOfPermissions > originalNrOfPermissions) {
				// persist the collection as we have "really" added new permission(s)
				persistPermissionCollection = true;
				LOG.debug("Added the following permissions to an existing permission collection: "
						+ permissionsSet);
			}
		}

		if(persistPermissionCollection) {
			// persist the permission collection
			permissionCollectionService.saveOrUpdate(groupPermissionCollection);
			LOG.debug("Persisted a permission collection");

			// persist the entity if necessary
			if(persistEntity) {
				this.saveOrUpdate(entity);
				LOG.debug("Persisted the entity with a new permission collection.");
			}
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
	public void removeAndSaveGroupPermissions(E entity, UserGroup userGroup, Permission... permissions) {
		if(entity == null) {
			LOG.error("Could not remove permissions: The passed entity is NULL.");
			return;
		}

		// create a set from the passed array
		final HashSet<Permission> permissionsSet = new HashSet<Permission>(Arrays.asList(permissions));

		if(permissionsSet == null || permissionsSet.isEmpty()) {
			LOG.error("Could not remove permissions: No permissions have been passed." );
			return;
		}

		// get the existing permission
		PermissionCollection groupPermissionCollection = entity.getGroupPermissions().get(userGroup);

		if(groupPermissionCollection == null) {
			LOG.error("Could not remove permissions as there is no attached permission collection.");
			return;
		}

		Set<Permission> groupPermissions = groupPermissionCollection.getPermissions();

		int originalNrOfPermissions = groupPermissions.size();

		// remove the passed permissions from the the existing permission collection
		groupPermissions.removeAll(permissionsSet);

		int newNrOfPermissions = groupPermissions.size();

		if(newNrOfPermissions == 0) {
			// remove
			entity.getGroupPermissions().remove(userGroup);
			this.saveOrUpdate(entity);

			LOG.debug("The permission collection is empty and will thereby be deleted now.");
			permissionCollectionService.delete(groupPermissionCollection);

			return;
		}

		// only persist if we have "really" removed something
		if(newNrOfPermissions < originalNrOfPermissions) {
			LOG.debug("Removed the following permissions from an existing permission collection: "
					+ permissionsSet);
			// persist the permission collection
			permissionCollectionService.saveOrUpdate(groupPermissionCollection);
			LOG.debug("Persisted a permission collection");
		}
	}

	/**
	 * This method returns a {@link Map} that maps {@link PersistentObject}s
	 * to PermissionCollections for the passed {@link User}. I.e. the keySet
	 * of the map is the collection of all {@link PersistentObject}s where the
	 * user has at least one permission and the corresponding value contains
	 * the {@link PermissionCollection} for the passed user on the entity.
	 *
	 * @param user
	 * @return
	 */
	@PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#user, 'READ')")
	public Map<PersistentObject, PermissionCollection> findAllUserPermissionsOfUser(User user) {
		return dao.findAllUserPermissionsOfUser(user);
	}

	/**
	 * This method returns a {@link Map} that maps {@link PersistentObject}s
	 * to PermissionCollections for the passed {@link UserGroup}. I.e. the keySet
	 * of the map is the collection of all {@link PersistentObject}s where the
	 * user group has at least one permission and the corresponding value contains
	 * the {@link PermissionCollection} for the passed user group on the entity.
	 *
	 * @param userGroup
	 * @return
	 */
	@PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#userGroup, 'READ')")
	public Map<PersistentObject, PermissionCollection> findAllUserGroupPermissionsOfUserGroup(UserGroup userGroup) {
		return dao.findAllUserGroupPermissionsOfUserGroup(userGroup);
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
