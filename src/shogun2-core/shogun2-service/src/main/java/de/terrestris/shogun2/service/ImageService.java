package de.terrestris.shogun2.service;

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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.terrestris.shogun2.dao.ImageDao;
import de.terrestris.shogun2.model.Image;

/**
 * Service class for the {@link Image} model.
 *
 * @author Daniel Koch
 * @author Johannes Weskamm
 *
 */
@Service("imageService")
public class ImageService<E extends Image, D extends ImageDao<E>>
		extends FileService<E, D> {

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("imageDao")
	public void setDao(D dao) {
		this.dao = dao;
	}

	/**
	 * Method persists a given Image as a bytearray in the database
	 *
	 * @param file
	 * @param resize
	 * @param imageSize
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Image uploadImage(MultipartFile file, boolean createThumbnail, Integer dimensions)
			throws Exception {

		InputStream is = null;
		ByteArrayInputStream bais = null;
		byte[] imageByteArray = null;
		byte[] thumbnail = null;
		Image imageToPersist = new Image();

		try {
			is = file.getInputStream();
			imageByteArray = IOUtils.toByteArray(is);

			// create a thumbnail if requested
			if (createThumbnail) {
				thumbnail = scaleImage(
					imageByteArray,
					FilenameUtils.getExtension(file.getOriginalFilename()),
					dimensions);
				imageToPersist.setThumbnail(thumbnail);
			}
			imageToPersist.setFile(imageByteArray);

			//detect dimensions
			bais = new ByteArrayInputStream(imageByteArray);
			BufferedImage bimg = ImageIO.read(bais);
			imageToPersist.setWidth(bimg.getWidth());
			imageToPersist.setHeight(bimg.getHeight());

			imageToPersist.setFileType(file.getContentType());
			imageToPersist.setFileName(file.getOriginalFilename());
			dao.saveOrUpdate((E) imageToPersist);
		} catch(Exception e) {
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
	 * @param is
	 * @param outputFormat
	 * @param outputSize
	 * @return
	 * @throws Exception
	 */
	public static byte[] scaleImage(byte[] imageBytes, String outputFormat,
			Integer outputSize) throws Exception {

		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] imageInByte = null;
		BufferedImage image = null;
		BufferedImage resizedImage = null;

		try {
			is = new ByteArrayInputStream(imageBytes);
			image = ImageIO.read(is);
			resizedImage = Scalr.resize(image, outputSize);
			ImageIO.write(resizedImage, outputFormat, baos);
			imageInByte = baos.toByteArray();
		} catch(Exception e) {
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
		return imageInByte;
	}

	/**
	 * Method gets a persisted image by the given id
	 *
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Image getImage(Integer id) throws Exception {

		Image persistedImage = null;

		LOG.debug("Requested to return an image");

		try {
			// get the file entity
			persistedImage = this.findById(id);

			if (persistedImage != null) {
				LOG.debug("Successfully returned an image");
			} else {
				throw new Exception("Could not find the image with id " + id);
			}

		} catch (Exception e) {
			LOG.error("Could not return the requested image: " +
					e.getMessage());
			throw new Exception("Could not return the image with id " + id);
		}
		return persistedImage;
	}

}
