/**
 *
 */
package de.terrestris.shogun2.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.terrestris.shogun2.dao.WpsProcessExecuteDao;
import de.terrestris.shogun2.model.wps.WpsPlugin;
import de.terrestris.shogun2.model.wps.WpsProcessExecute;
import de.terrestris.shogun2.service.WpsProcessExecuteService;
import de.terrestris.shogun2.util.data.ResultSet;

/**
 *
 * @author Nils Bühner
 *
 */
@Controller
@RequestMapping("/wpsprocessexecutes")
public class WpsProcessExecuteController<E extends WpsProcessExecute, D extends WpsProcessExecuteDao<E>, S extends WpsProcessExecuteService<E, D>>
		extends WpsReferenceController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public WpsProcessExecuteController() {
		this((Class<E>) WpsProcessExecute.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected WpsProcessExecuteController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("wpsProcessExecuteService")
	public void setService(S service) {
		this.service = service;
	}

	/**
	 * Checks in which {@link WpsPlugin}s the given {@link WpsProcessExecute} is
	 * contained (and from which it would be "disconnected" in case of
	 * deletion).
	 *
	 * @param wpsProcessId
	 *            ID of the {@link WpsProcessExecute}
	 * @return
	 */
	@RequestMapping(value="preCheckDelete.action", method = RequestMethod.POST)
	public ResponseEntity<?> preCheckDelete(@RequestParam("wpsProcessId") Integer wpsProcessId) {
		List<String> result = null;
		try {
			result = service.preCheckDelete(wpsProcessId);
		} catch (Exception e) {
			final String msg = e.getMessage();
			LOG.error("Could not pre-check WpsProcessExecute deletion: " + msg);
			return new ResponseEntity<>(ResultSet.error(msg), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(ResultSet.success(result), HttpStatus.OK);
	}

}
