package de.terrestris.shogun2.service;

import org.h2.util.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.PluginDao;
import de.terrestris.shogun2.model.Plugin;

/**
 * Service class for the {@link Plugin} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
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
	 *
	 * @param simpleClassName
	 * @return
	 * @throws Exception
	 */
	public String getPluginSource(String simpleClassName) throws Exception {

		// qualify className
		String className = pluginNamespace + "." + simpleClassName;

		Criterion criterion = Restrictions.eq("className", className);
		final E plugin = dao.findByUniqueCriteria(criterion);

		String code = null;
		if(plugin != null) {
			code = plugin.getSourceCode();
		}

		if(StringUtils.isNullOrEmpty(code)) {
			throw new Exception("Could not retrieve code for '" + className + "'");
		}

		return code;
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
}
