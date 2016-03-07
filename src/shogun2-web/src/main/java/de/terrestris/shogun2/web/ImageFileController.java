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
	 * Set this flag to false to avoid creating thumbnails for uploaded images
	 */
	private boolean createThumbnail = true;

	/**
	 * The default value used for the creation of thumbnails in pixels
	 */
	private Integer thumbnailDimensions = 100;

	/**
	 * Persists an image as bytearray in the database
	 *
	 * @param uploadedImage
	 * @return
	 */
	@RequestMapping(value = "/upload.action", method = RequestMethod.POST)
	public ResponseEntity<String> uploadFile(
			@RequestParam("file") MultipartFile uploadedImage) {

		LOG.debug("Requested to upload an image");

		Map<String, Object> responseMap = new HashMap<String, Object>();
		final HttpHeaders responseHeaders = new HttpHeaders();
		HttpStatus responseStatus = HttpStatus.OK;
		String responseMapAsString = null;
		ObjectMapper mapper = new ObjectMapper();

		// we have to return the response-Map as String to be browser conform.
		// as this controller is typically being called by a form.submit() the
		// browser expects a response with the Content-Type header set to
		// "text/html".
		responseHeaders.setContentType(MediaType.TEXT_HTML);

		if (uploadedImage.isEmpty()) {
			LOG.error("Upload failed. Image " + uploadedImage + " is empty.");
			responseMap = ResultSet.error("Upload failed. Image " +
					uploadedImage.getOriginalFilename() + " is empty.");
		}

		try {
			ImageFile image = service.uploadImage(
					uploadedImage, createThumbnail, thumbnailDimensions);
			LOG.info("Successfully uploaded image " + image.getFileName());
			responseMap = ResultSet.success(image);
		} catch (Exception e) {
			LOG.error("Could not upload the image: " + e.getMessage());
			responseMap = ResultSet.error("Could not upload the image: " +
					e.getMessage());
		}

		// rewrite the response-Map as String
		try {
			responseMapAsString = mapper.writeValueAsString(responseMap);
		} catch (JsonProcessingException e) {
			LOG.error("Error while rewriting the response Map to a String" +
					e.getMessage());
			responseMap = ResultSet.error("Error while rewriting the " +
					"response Map to a String" + e.getMessage());
		}

		return new ResponseEntity<String>(responseMapAsString, responseHeaders,
				responseStatus);
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
}
