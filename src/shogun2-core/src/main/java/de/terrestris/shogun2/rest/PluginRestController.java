package de.terrestris.shogun2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.PluginDao;
import de.terrestris.shogun2.model.Plugin;
import de.terrestris.shogun2.service.PluginService;

/**
 * @author Nils Bühner
 *
 */
@RestController
@RequestMapping("/plugins")
public class PluginRestController<E extends Plugin, D extends PluginDao<E>, S extends PluginService<E, D>>
		extends AbstractRestController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public PluginRestController() {
		this((Class<E>) Plugin.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected PluginRestController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 *
	 * @return
	 */
	@RequestMapping(value="/{simpleClassName}.js", method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getExternalPluginSource(
			@PathVariable String simpleClassName) {

		LOG.debug("Requested to return the sourcecode for plugin " + simpleClassName);

		ResponseEntity<String> responseEntity = null;
		HttpHeaders responseHeaders = new HttpHeaders();

		try {
			responseHeaders.add("Content-Type", "text/javascript; charset=utf-8");

			String classCode = service.getPluginSource(simpleClassName);

			responseEntity = new ResponseEntity<String>(
					classCode,
					responseHeaders,
					HttpStatus.OK
			);

		} catch(Exception e) {
			responseHeaders.add("Content-Type", "text/*");

			String errMsg = "Error while returning the class code for "
					+ "external plugin: " + e.getMessage();

			LOG.error(errMsg);

			responseEntity = new ResponseEntity<String>(
					errMsg,
					responseHeaders,
					HttpStatus.INTERNAL_SERVER_ERROR
			);
		}

		return responseEntity;
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("pluginService")
	public void setService(S service) {
		this.service = service;
	}
}
