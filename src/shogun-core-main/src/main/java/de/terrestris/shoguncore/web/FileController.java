package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.dao.FileDao;
import de.terrestris.shoguncore.model.File;
import de.terrestris.shoguncore.service.FileService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author Johannes Weskamm
 * @author Daniel Koch
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
     * Persists a file as bytearray in the database
     *
     * @param uploadedFile
     */
    @RequestMapping(value = "/upload.action", method = RequestMethod.POST)
    public ResponseEntity<?> uploadFile(
        @RequestParam("file") MultipartFile uploadedFile) {

        logger.debug("Requested to upload a multipart-file");

        // build response map
        Map<String, Object> responseMap = null;
        try {
            E file = service.uploadFile(uploadedFile);
            logger.info("Successfully uploaded file " + file.getFileName());
            responseMap = ResultSet.success(file);
        } catch (Exception e) {
            logger.error("Could not upload the file: " + e.getMessage());
            responseMap = ResultSet.error("Could not upload the file: " +
                e.getMessage());
        }

        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        // rewrite the response-Map as String
        return new ResponseEntity<>(responseMap, responseHeaders, HttpStatus.OK);
    }

    /**
     * Gets a file from the database by the given id
     */
    @RequestMapping(value = "/get.action", method = RequestMethod.GET)
    public ResponseEntity<?> getFile(@RequestParam Integer id) {
        final HttpHeaders responseHeaders = new HttpHeaders();
        try {
            File file = service.findById(id);

            if (file == null) {
                throw new Exception("Could not find the file with id " + id);
            }

            byte[] fileBytes = file.getFile();

            responseHeaders.setContentType(
                MediaType.parseMediaType(file.getFileType()));

            logger.info("Successfully got the file " + file.getFileName());
            return new ResponseEntity<>(fileBytes, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Could not get the file: " + e.getMessage());
            Map<String, Object> responseMap = ResultSet.error("Could not get the file: " +
                e.getMessage());

            responseHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(responseMap, responseHeaders, HttpStatus.OK);
        }
    }
}
