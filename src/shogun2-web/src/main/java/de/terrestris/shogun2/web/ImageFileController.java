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

import de.terrestris.shogun2.dao.ImageFileDao;
import de.terrestris.shogun2.model.ImageFile;
import de.terrestris.shogun2.service.ImageFileService;
import de.terrestris.shogun2.util.data.ResultSet;
import de.terrestris.shogun2.util.json.Shogun2JsonObjectMapper;

/**
 *
 * @author Johannes Weskamm
 * @author Daniel Koch
 *
 */
@Controller
@RequestMapping("/image")
public class ImageFileController<E extends ImageFile, D extends ImageFileDao<E>, S extends ImageFileService<E, D>>
		extends FileController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public ImageFileController() {
		this((Class<E>) ImageFile.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected ImageFileController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("imageFileService")
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
	 * Persists an image as bytearray in the database
	 *
	 * @param uploadedImage
	 * @return
	 */
	@Override
	@RequestMapping(value = "/upload.action", method = RequestMethod.POST)
	public ResponseEntity<String> uploadFile(
			@RequestParam("file") MultipartFile uploadedImage) {

		LOG.debug("Requested to upload an image");

		// build response map
		Map<String, Object> responseMap = new HashMap<String, Object>();
		try {
			ImageFile imageFile = service.uploadImageFile(uploadedImage);
			responseMap = ResultSet.success(imageFile);
		} catch (Exception e) {
			responseMap = ResultSet.error(e.getMessage());
		}

		// we have to return the response-Map as String to be browser conform.
		// as this controller is typically being called by a form.submit() the
		// browser expects a response with the Content-Type header set to
		// "text/html".
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_HTML);

		// rewrite the response map as String
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
	 * Gets an image from the database by the given id
	 *
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping(value = "/getThumbnail.action", method=RequestMethod.GET)
	public ResponseEntity<?> getThumbnail(@RequestParam Integer id) {

		final HttpHeaders responseHeaders = new HttpHeaders();
		Map<String, Object> responseMap = new HashMap<String, Object>();

		try {
			// try to get the image
			ImageFile image = service.findById(id);
			if(image == null) {
				throw new Exception("Could not find the image with id " + id);
			}

			byte[] imageBytes = null;

			imageBytes = image.getThumbnail();

			responseHeaders.setContentType(
					MediaType.parseMediaType(image.getFileType()));

			LOG.info("Successfully got the image thumbnail " +
					image.getFileName());

			return new ResponseEntity<byte[]>(
					imageBytes, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			final String errorMessage = "Could not get the image thumbnail: "
					+ e.getMessage();

			LOG.error(errorMessage);
			responseMap = ResultSet.error(errorMessage);

			responseHeaders.setContentType(MediaType.APPLICATION_JSON);

			return new ResponseEntity<Map<String, Object>>(
					responseMap, responseHeaders, HttpStatus.OK);
		}
	}

	/**
	 * @return the objectMapper
	 */
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
}
