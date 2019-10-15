package de.terrestris.shoguncore.security.access.entity;

import de.terrestris.shoguncore.model.PersistentObject;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.UserGroup;
import de.terrestris.shoguncore.model.security.Permission;
import de.terrestris.shoguncore.model.security.PermissionCollection;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author Nils Bühner
 * @author Johannes Weskamm
 */
public class PersistentObjectPermissionEvaluator<E extends PersistentObject> {

    /**
     * The LOGGER instance
     */
    protected static final Logger logger = getLogger(PersistentObjectPermissionEvaluator.class);

    /**
     * Represents the class of the entity
     */
    private final Class<E> entityClass;

    /**
     * @param entityClass
     */
    public PersistentObjectPermissionEvaluator(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @param entity
     * @param permission
     */
    public boolean hasPermission(User user, E entity, Permission permission) {

        final String simpleClassName = entityClass.getSimpleName();

        // CHECK USER PERMISSIONS
        final Map<User, PermissionCollection> userPermissionsMap = entity.getUserPermissions();

        PermissionCollection userPermissionCol = extractUserPermissions(user, userPermissionsMap);
        final Set<Permission> userPermissions = userPermissionCol.getPermissions();

        // grant access if user explicitly has the requested permission or
        // if the user has the ADMIN permission
        if (userPermissions.contains(permission)
            || userPermissions.contains(Permission.ADMIN)) {
            logger.trace("Granting " + permission
                + " access by user permissions");
            return true;
        }

        // CHECK GROUP PERMISSIONS
        final Map<UserGroup, PermissionCollection> groupPermissionsMap = entity.getGroupPermissions();

        PermissionCollection groupPermissionsCol = extractGroupPermissions(user, groupPermissionsMap);
        final Set<Permission> groupPermissions = groupPermissionsCol.getPermissions();

        // grant access if group explicitly has the requested permission or
        // if the group has the ADMIN permission
        if (groupPermissions.contains(permission)
            || groupPermissions.contains(Permission.ADMIN)) {
            logger.trace("Granting " + permission
                + " access by group permissions");
            return true;
        }

        logger.trace("Restricting " + permission + " access on secured object '"
            + simpleClassName + "' with ID " + entity.getId());
        return false;
    }

    /**
     * @param userPermissionsMap
     */
    protected PermissionCollection extractUserPermissions(User user,
                                                          Map<User, PermissionCollection> userPermissionsMap) {

        PermissionCollection permissionCollection = userPermissionsMap.get(user);

        if (permissionCollection == null) {
            // return empty collection
            permissionCollection = new PermissionCollection();
        }

        return permissionCollection;
    }

    /**
     * @param groupPermissionsMap
     */
    protected PermissionCollection extractGroupPermissions(User user, Map<UserGroup, PermissionCollection> groupPermissionsMap) {
        Set<Permission> aggregatedGroupPermissions = new HashSet<>();

        for (Map.Entry<UserGroup, PermissionCollection> userGroupEntry: groupPermissionsMap.entrySet()) {
            UserGroup userGroup = userGroupEntry.getKey();
            if (userGroup.getMembers().contains(user)) {
                Set<Permission> groupPermissions =userGroupEntry.getValue().getPermissions();
                aggregatedGroupPermissions.addAll(groupPermissions);
            }
        }
        return new PermissionCollection(aggregatedGroupPermissions);
    }

    /**
     * @return the entityClass
     */
    public Class<E> getEntityClass() {
        return entityClass;
    }

}
