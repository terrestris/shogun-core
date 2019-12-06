package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.ApplicationDao;
import de.terrestris.shoguncore.dao.PluginDao;
import de.terrestris.shoguncore.model.Application;
import de.terrestris.shoguncore.model.Plugin;
import org.h2.util.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for the {@link Plugin} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("pluginService")
public class PluginService<E extends Plugin, D extends PluginDao<E>> extends
    PermissionAwareCrudService<E, D> {

    /**
     * The name of the plugin namespace.
     */
    @Value("${plugin.namespace:Plugin}")
    private String pluginNamespace;

    /**
     * The application service which we e.g. need when plugins are deleted.
     */
    @Autowired
    @Qualifier("applicationService")
    private ApplicationService<Application, ApplicationDao<Application>> applicationService;

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public PluginService() {
        this((Class<E>) Plugin.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected PluginService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * @return the pluginNamespace
     */
    public String getPluginNamespace() {
        return pluginNamespace;
    }

    /**
     * @param pluginNamespace the pluginNamespace to set
     */
    public void setPluginNamespace(String pluginNamespace) {
        this.pluginNamespace = pluginNamespace;
    }

    /**
     * @param simpleClassName
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public String getPluginSource(String simpleClassName) throws Exception {

        // qualify className
        String className = pluginNamespace + "." + simpleClassName;

        Criterion criterion = Restrictions.eq("className", className);
        final E plugin = dao.findByUniqueCriteria(criterion);

        String code = null;
        if (plugin != null) {
            code = plugin.getSourceCode();
        }

        if (StringUtils.isNullOrEmpty(code)) {
            throw new Exception("Could not retrieve code for '" + className + "'");
        }

        return code;
    }

    /**
     * @param pluginId
     * @return List of application names that contain the given plugin
     */
    @PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#pluginId, 'de.terrestris.shoguncore.model.Plugin', 'DELETE')")
    @Transactional(readOnly = true)
    public List<String> preCheckDelete(Integer pluginId) {
        List<String> result = new ArrayList<>();
        E plugin = this.dao.findById(pluginId);
        if (plugin != null) {
            List<Application> appsWithPlugin = applicationService.findAllWithCollectionContaining("plugins", plugin);
            for (Application application : appsWithPlugin) {
                result.add(application.getName());
            }
        }
        return result;
    }

    /**
     * Removes the passed plugin from all applications and afterwards deletes the plugin itself. This overrides the
     * generic method to delete {@link AbstractCrudService#delete(de.terrestris.shoguncore.model.PersistentObject)}.
     *
     * @param plugin
     */
    @Override
    @PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#plugin, 'DELETE')")
    public void delete(E plugin) {
        if (applicationService == null) {
            logger.error("Plugin cannot be deleted, failed to autowire application service");
            return;
        }

        List<Application> apps = applicationService.findAllWithCollectionContaining("plugins", plugin);
        Integer pluginId = plugin.getId();
        for (Application app : apps) {
            List<Plugin> plugins = app.getPlugins();
            if (plugins != null && plugins.contains(plugin)) {
                String msg = String.format(
                    "Remove plugin (id=%s) from application (id=%s)",
                    pluginId, app.getId()
                );
                logger.debug(msg);
                plugins.remove(plugin);
                // TODO will this use the the PreAuthorize annotations of AbstractCrudService wrt WRITE on the app?
                applicationService.saveOrUpdate(app);
            }
        }
        logger.debug(String.format("Delete plugin (id=%s)", pluginId));
        // call overridden parent to actually delete the entity itself
        super.delete(plugin);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("pluginDao")
    public void setDao(D dao) {
        this.dao = dao;
    }

    /**
     * Sets the applicationService.
     *
     * @param applicationService
     */
    public void setApplicationService(ApplicationService<Application, ApplicationDao<Application>> applicationService) {
        this.applicationService = applicationService;
    }
}
