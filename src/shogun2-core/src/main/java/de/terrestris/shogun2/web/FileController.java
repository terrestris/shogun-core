package de.terrestris.shogun2.web;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.terrestris.shogun2.dao.FileDao;
import de.terrestris.shogun2.model.File;
import de.terrestris.shogun2.service.FileService;
import de.terrestris.shogun2.util.data.ResultSet;
import de.terrestris.shogun2.util.json.Shogun2JsonObjectMapper;

/**
 *
 * @author Johannes Weskamm
 * @author Daniel Koch
 *
 */
@Controller
@RequestMapping("/file")
public class FileController<E extends File, D extends FileDao<E>, S extends FileService<E, D>>
		extends AbstractWebController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public FileController() {
		this((Class<E>) File.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected FileController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("fileService")
	public void setService(S service) {
		this.service = service;
	}

	/**
	 * Use the object mapper from the spring context, if available (e.g.
	 * {@link Shogun2JsonObjectMapper}). If not available, the default
	 * implementation will be used.
	 */
	@Autowired(required = false)
	private ObjectMapper objectMapper;

	/**
	 * Persists a file as bytearray in the database
	 *
	 * @param uploadedFile
	 * @return
	 */
	@RequestMapping(value = "/upload.action", method = RequestMethod.POST)
	public ResponseEntity<String> uploadFile(
			@RequestParam("file") MultipartFile uploadedFile) {

		LOG.debug("Requested to upload a multipart-file");

		// build response map
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			E file = service.uploadFile(uploadedFile);
			LOG.info("Successfully uploaded file " + file.getFileName());
			responseMap = ResultSet.success(file);
		} catch (Exception e) {
			LOG.error("Could not upload the file: " + e.getMessage());
			responseMap = ResultSet.error("Could not upload the file: " +
					e.getMessage());
		}

		// we have to return the response-Map as String to be browser conform.
		// as this controller is typically being called by a form.submit() the
		// browser expects a response with the Content-Type header set to
		// "text/html".
		final HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_HTML);

		// rewrite the response-Map as String
		String responseMapAsString = null;
		try {
			// try to use autowired object mapper from spring context
			ObjectMapper om = (objectMapper != null) ? objectMapper : new ObjectMapper();
			responseMapAsString = om.writeValueAsString(responseMap);
		} catch (JsonProcessingException e) {
			String errMsg = "Error while rewriting the response Map to a String: " + e.getMessage();
			LOG.error(errMsg);

			// use errorMsg if serialization as json failed
			responseMapAsString = errMsg;
		}

		return new ResponseEntity<String>(responseMapAsString, responseHeaders, HttpStatus.OK);
	}

	/**
	 * Gets a file from the database by the given id
	 *
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping(value = "/get.action", method=RequestMethod.GET)
	public ResponseEntity<?> getFile(@RequestParam Integer id) {

		final HttpHeaders responseHeaders = new HttpHeaders();
		Map<String, Object> responseMap = new HashMap<String, Object>();

		try {
			File file = service.findById(id);

			if(file == null) {
				throw new Exception("Could not find the file with id " + id);
			}

			byte[] fileBytes = file.getFile();

			responseHeaders.setContentType(
					MediaType.parseMediaType(file.getFileType()));

			LOG.info("Successfully got the file " + file.getFileName());

			responseMap = ResultSet.success(file);
			return new ResponseEntity<byte[]>(
					fileBytes, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Could not get the file: " + e.getMessage());
			responseMap = ResultSet.error("Could not get the file: " +
					e.getMessage());

			responseHeaders.setContentType(MediaType.APPLICATION_JSON);

			return new ResponseEntity<Map<String, Object>>(
					responseMap, responseHeaders, HttpStatus.OK);
		}
	}
}
