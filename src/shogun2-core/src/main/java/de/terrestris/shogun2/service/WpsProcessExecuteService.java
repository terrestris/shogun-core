package de.terrestris.shogun2.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.WpsPluginDao;
import de.terrestris.shogun2.dao.WpsProcessExecuteDao;
import de.terrestris.shogun2.model.wps.WpsPlugin;
import de.terrestris.shogun2.model.wps.WpsProcessExecute;

/**
 * Service class for the {@link WpsProcessExecute} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("wpsProcessExecuteService")
public class WpsProcessExecuteService<E extends WpsProcessExecute, D extends WpsProcessExecuteDao<E>> extends
		WpsReferenceService<E, D> {

	/**
	 * The WpsPluginService which we e.g. need when WpsProcessExecutes are deleted.
	 */
	@Autowired
	@Qualifier("wpsPluginService")
	private WpsPluginService<WpsPlugin, WpsPluginDao<WpsPlugin>> wpsPluginService;

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public WpsProcessExecuteService() {
		this((Class<E>) WpsProcessExecute.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected WpsProcessExecuteService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * Removes the passed WpsProcessExecute from all WpsPlugins and afterwards
	 * deletes the WpsProcessExecute itself. This overrides the generic method
	 * to delete {@link AbstractCrudService#delete(de.terrestris.shogun2.model.PersistentObject)}.
	 *
	 * @param wpsProcessExecute
	 */
	@Override
	@PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#plugin, 'DELETE')")
	public void delete(E wpsProcessExecute) {
		if (wpsPluginService == null) {
			LOG.error("WPSProcessExecute cannot be deleted, failed to autowire WpsPluginService");
			return;
		}

		WpsPluginDao<WpsPlugin> wpsPluginDao = wpsPluginService.getDao();

		SimpleExpression eqProcess = Restrictions.eq("process", wpsProcessExecute);
		List<WpsPlugin> wpsPlugins = wpsPluginDao.findByCriteria(eqProcess);

		Integer processId = wpsProcessExecute.getId();
		for (WpsPlugin wpsPlugin : wpsPlugins) {
			WpsProcessExecute process = wpsPlugin.getProcess();
			if (process != null) {
				String msg = String.format(
					"Remove WpsProcessExecute (id=%s) from WpsPlugin (id=%s)",
					processId, wpsPlugin.getId()
				);
				LOG.debug(msg);
				wpsPlugin.setProcess(null);
				wpsPluginService.saveOrUpdate(wpsPlugin);
			}
		}
		LOG.debug(String.format("Delete plugin (id=%s)", processId));

		// Call overridden parent to actually delete the entity itself
		super.delete(wpsProcessExecute);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("wpsProcessExecuteDao")
	public void setDao(D dao) {
		this.dao = dao;
	}

	/**
	 *
	 * @param wpsId
	 * @return List of {@link WpsPlugin}s that are connected to the given {@link WpsProcessExecute}
	 */
	@PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName()) or hasPermission(#wpsId, 'de.terrestris.shogun2.model.wps.WpsProcessExecute', 'DELETE')")
	@Transactional(readOnly = true)
	public List<String> preCheckDelete(Integer wpsId) {
		List<String> result = new ArrayList<>();

		E wpsProcessExecute = this.dao.findById(wpsId);

		if (wpsProcessExecute != null) {

			List<WpsPlugin> pluginsWithWps = wpsPluginService.findAllWhereFieldEquals("process", wpsProcessExecute);

			for (WpsPlugin plugin : pluginsWithWps) {
				result.add(plugin.getName());
			}

		}

		return result;
	}

	/**
	 * @return the wpsPluginService
	 */
	public WpsPluginService<WpsPlugin, WpsPluginDao<WpsPlugin>> getWpsPluginService() {
		return wpsPluginService;
	}
}
