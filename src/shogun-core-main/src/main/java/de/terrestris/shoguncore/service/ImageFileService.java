package de.terrestris.shoguncore.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.terrestris.shoguncore.dao.ImageFileDao;
import de.terrestris.shoguncore.model.ImageFile;

/**
 * Service class for the {@link ImageFile} model.
 *
 * @author Daniel Koch
 * @author Johannes Weskamm
 */
@Service("imageFileService")
public class ImageFileService<E extends ImageFile, D extends ImageFileDao<E>>
    extends FileService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public ImageFileService() {
        this((Class<E>) ImageFile.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected ImageFileService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("imageFileDao")
    public void setDao(D dao) {
        this.dao = dao;
    }

    /**
     * The default value used for the creation of thumbnails in pixels
     */
    private final Integer DEFAULT_THUMBNAIL_SIZE = 100;

    /**
     * @param file
     * @throws Exception
     */
    @Override
    @PreAuthorize("isAuthenticated()")
    public E uploadFile(MultipartFile file) throws Exception {

        if (file == null) {
            final String errMsg = "Upload failed. Image is null.";
            LOG.error(errMsg);
            throw new Exception(errMsg);
        } else if (file.isEmpty()) {
            final String errMsg = "Upload failed. Image " + file + " is empty.";
            LOG.error(errMsg);
            throw new Exception(errMsg);
        }

        // persist the image file
        E image = this.saveImage(file, true, DEFAULT_THUMBNAIL_SIZE);
        LOG.info("Successfully uploaded image " + image.getFileName());

        return image;
    }

    /**
     * Method persists a given Image as a bytearray in the database
     *
     * @param file
     * @param createThumbnail
     * @param thumbnailTargetSize
     * @throws Exception
     */
    @PreAuthorize("isAuthenticated()")
    public E saveImage(MultipartFile file, boolean createThumbnail, Integer thumbnailTargetSize)
        throws Exception {

        InputStream is = null;
        ByteArrayInputStream bais = null;
        E imageToPersist = null;

        try {
            is = file.getInputStream();
            byte[] imageByteArray = IOUtils.toByteArray(is);

            // create a new instance (generic)
            imageToPersist = getEntityClass().newInstance();

            // create a thumbnail if requested
            if (createThumbnail) {
                byte[] thumbnail = scaleImage(
                    imageByteArray,
                    FilenameUtils.getExtension(file.getOriginalFilename()),
                    thumbnailTargetSize);
                imageToPersist.setThumbnail(thumbnail);
            }

            // set binary image data
            imageToPersist.setFile(imageByteArray);

            // detect dimensions
            bais = new ByteArrayInputStream(imageByteArray);

            BufferedImage bimg = ImageIO.read(bais);

            // set basic image properties
            imageToPersist.setWidth(bimg.getWidth());
            imageToPersist.setHeight(bimg.getHeight());
            imageToPersist.setFileType(file.getContentType());
            imageToPersist.setFileName(file.getOriginalFilename());

            // persist the image
            dao.saveOrUpdate(imageToPersist);

        } catch (Exception e) {
            throw new Exception("Could not create the Image in DB: "
                + e.getMessage());
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(bais);
        }

        return imageToPersist;
    }

    /**
     * Scales an image by the given dimensions
     *
     * @param outputFormat
     * @param targetSize   width/height in px (square)
     * @throws Exception
     */
    public static byte[] scaleImage(byte[] imageBytes, String outputFormat,
                                    Integer targetSize) throws Exception {

        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageInBytes = null;
        BufferedImage image = null;
        BufferedImage resizedImage = null;

        try {
            is = new ByteArrayInputStream(imageBytes);
            image = ImageIO.read(is);
            resizedImage = Scalr.resize(image, targetSize);
            ImageIO.write(resizedImage, outputFormat, baos);
            imageInBytes = baos.toByteArray();
        } catch (Exception e) {
            throw new Exception("Error on resizing an image: " + e.getMessage());
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(baos);
            if (image != null) {
                image.flush();
            }
            if (resizedImage != null) {
                resizedImage.flush();
            }
        }
        return imageInBytes;
    }

}
