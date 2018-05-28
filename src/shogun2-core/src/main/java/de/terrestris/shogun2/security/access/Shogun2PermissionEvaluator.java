package de.terrestris.shogun2.security.access;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.dao.UserDao;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.security.Permission;
import de.terrestris.shogun2.security.access.entity.PersistentObjectPermissionEvaluator;
import de.terrestris.shogun2.security.access.factory.EntityPermissionEvaluatorFactory;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.Collection;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author Nils Bühner
 */
public class Shogun2PermissionEvaluator implements PermissionEvaluator {

    /**
     * The LOGGER instance
     */
    private final static Logger LOG = getLogger(Shogun2PermissionEvaluator.class);

    @Autowired
    private ApplicationContext appContext;

    /**
     * We have to use the DAO here. If we would use the service, we would end
     * with StackOverflow errors as a call to (secured) service methods triggers
     * this PermissionEvaluator class.
     */
    @Autowired
    @Qualifier("userDao")
    private UserDao<User> userDao;

    /**
     * If set to true, the plain principal object from the spring security
     * context will be used as "user". Otherwise the "full" user object will be
     * loaded from the SHOGun2 database.
     * <p>
     * It may be helpful to set this to true, if the user/principal object from
     * the security context contains information that is not persisted in the
     * database.
     * <p>
     * Setting this to the null value is the same as using "false".
     */
    private Boolean usePlainPrincipal = false;

    /**
     *
     */
    @SuppressWarnings("rawtypes")
    private EntityPermissionEvaluatorFactory permissionEvaluatorFactory;

    /**
     *
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Object targetDomainObject, Object permissionObject) {

        boolean hasPermission = false;

        if (authentication != null
            && targetDomainObject != null
            && targetDomainObject instanceof PersistentObject
            && permissionObject instanceof String) {

            User user = null;

            final Object principalObject = authentication.getPrincipal();

            if (principalObject instanceof User) {
                final User principal = (User) principalObject;

                if (usePlainPrincipal == true) {
                    user = principal;
                } else {
                    // get the "full" user from the database
                    user = userDao.findById(principal.getId());
                }
            }

            final PersistentObject persistentObject = (PersistentObject) targetDomainObject;
            final Integer objectId = persistentObject.getId();
            final String simpleClassName = targetDomainObject.getClass().getSimpleName();
            final Permission permission = Permission.fromString((String) permissionObject);

            String accountName = (user == null) ? "ANONYMOUS" : user.getAccountName();

            LOG.trace("Evaluating whether user '" + accountName
                + "' has permission '" + permission + "' on '"
                + simpleClassName + "' with ID " + objectId);

            PersistentObjectPermissionEvaluator entityPermissionEvaluator = permissionEvaluatorFactory
                .getEntityPermissionEvaluator(persistentObject.getClass());

            hasPermission = entityPermissionEvaluator.hasPermission(user, persistentObject, permission);

        } else {
            LOG.error("Permission evaluation has been aborted.");
        }

        return hasPermission;
    }

    /**
     *
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId, String targetType, Object permission) {

        Class<?> entityClass;

        try {
            entityClass = Class.forName(targetType);
        } catch (ClassNotFoundException e) {
            LOG.error("Could not create class for type: " + targetType + "(" + e.getMessage() + ")");
            return false;
        }

        // get all available DAOs from the app context
        Collection<GenericHibernateDao> allDaos = appContext.getBeansOfType(GenericHibernateDao.class).values();

        // the DAO we'll use to get the entity from the database
        GenericHibernateDao daoToUse = null;

        // we'll first try to find the exact matching DAO
        for (GenericHibernateDao dao : allDaos) {
            if (dao.getEntityClass().equals(entityClass)) {
                // we found a matching DAO
                daoToUse = dao;
                LOG.debug("Found an exactly matching DAO for type " + entityClass);
                break;
            }
        }

        // if we could not find an exact match, we'll try to use the "next best"
        // from the entity hierarchy
        if (daoToUse == null) {
            for (GenericHibernateDao dao : allDaos) {
                if (dao.getEntityClass().isAssignableFrom(entityClass)) {
                    // we found a DAO that will work (e.g. PersonDao for User.class)
                    daoToUse = dao;
                    LOG.debug("Found a matching DAO from the hierarchy of type " + entityClass);
                    break;
                }
            }
        }

        // LOG warning if we could NOT find a matching DAO
        if (daoToUse == null) {
            LOG.warn("Could not find a DAO for type:" + entityClass);
            return false;
        }

        // finally get the entity from the DB
        PersistentObject entity = daoToUse.findById(targetId);

        // call implementation based on entity
        return this.hasPermission(authentication, entity, permission);
    }

    /**
     * @return the appContext
     */
    public ApplicationContext getAppContext() {
        return appContext;
    }

    /**
     * @param appContext the appContext to set
     */
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    /**
     * @return the userDao
     */
    public UserDao<User> getUserDao() {
        return userDao;
    }

    /**
     * @param userDao the userDao to set
     */
    public void setUserDao(UserDao<User> userDao) {
        this.userDao = userDao;
    }

    /**
     * @return the usePlainPrincipal
     */
    public Boolean getUsePlainPrincipal() {
        return usePlainPrincipal;
    }

    /**
     * @param usePlainPrincipal the usePlainPrincipal to set
     */
    public void setUsePlainPrincipal(Boolean usePlainPrincipal) {
        this.usePlainPrincipal = usePlainPrincipal;
    }

    /**
     * @return the permissionEvaluatorFactory
     */
    @SuppressWarnings("rawtypes")
    public EntityPermissionEvaluatorFactory getPermissionEvaluatorFactory() {
        return permissionEvaluatorFactory;
    }

    /**
     * @param permissionEvaluatorFactory the permissionEvaluatorFactory to set
     */
    @SuppressWarnings("rawtypes")
    public void setPermissionEvaluatorFactory(
        EntityPermissionEvaluatorFactory permissionEvaluatorFactory) {
        this.permissionEvaluatorFactory = permissionEvaluatorFactory;
    }

}
