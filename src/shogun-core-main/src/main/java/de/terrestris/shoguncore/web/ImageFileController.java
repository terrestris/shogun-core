package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.dao.ImageFileDao;
import de.terrestris.shoguncore.model.ImageFile;
import de.terrestris.shoguncore.service.ImageFileService;
import de.terrestris.shoguncore.util.data.ResultSet;
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

import java.util.Map;

/**
 * @author Johannes Weskamm
 * @author Daniel Koch
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
     * Gets an image from the database by the given id
     */
    @RequestMapping(value = "/getThumbnail.action", method = RequestMethod.GET)
    public ResponseEntity<?> getThumbnail(@RequestParam Integer id) {

        final HttpHeaders responseHeaders = new HttpHeaders();
        Map<String, Object> responseMap;

        try {
            // try to get the image
            ImageFile image = service.findById(id);
            if (image == null) {
                throw new Exception("Could not find the image with id " + id);
            }

            byte[] imageBytes = null;

            imageBytes = image.getThumbnail();

            responseHeaders.setContentType(
                MediaType.parseMediaType(image.getFileType()));

            logger.info("Successfully got the image thumbnail " +
                image.getFileName());

            return new ResponseEntity<byte[]>(
                imageBytes, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            final String errorMessage = "Could not get the image thumbnail: "
                + e.getMessage();

            logger.error(errorMessage);
            responseMap = ResultSet.error(errorMessage);

            responseHeaders.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<Map<String, Object>>(
                responseMap, responseHeaders, HttpStatus.OK);
        }
    }
}
