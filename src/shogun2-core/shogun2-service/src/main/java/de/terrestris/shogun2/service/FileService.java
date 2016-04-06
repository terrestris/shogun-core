package de.terrestris.shogun2.service;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import de.terrestris.shogun2.dao.FileDao;
import de.terrestris.shogun2.model.File;

/**
 * Service class for the {@link File} model.
 *
 * @author Daniel Koch
 * @author Johannes Weskamm
 *
 */
@Service("fileService")
public class FileService<E extends File, D extends FileDao<E>>
		extends AbstractCrudService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public FileService() {
		this((Class<E>) File.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected FileService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("fileDao")
	public void setDao(D dao) {
		this.dao = dao;
	}

	/**
	 * Method persists a given MultipartFile as a bytearray in the database
	 *
	 * @param file
	 * @param resize
	 * @param imageSize
	 * @return
	 * @throws Exception
	 */
	public E uploadFile(MultipartFile file) throws Exception {

		if (file == null) {
			final String errMsg = "Upload failed. File is null.";
			LOG.error(errMsg);
			throw new Exception(errMsg);
		} else if (file.isEmpty()) {
			final String errMsg = "Upload failed. File is empty.";
			LOG.error(errMsg);
			throw new Exception(errMsg);
		}

		InputStream is = null;
		byte[] fileByteArray = null;
		E fileToPersist = getEntityClass().newInstance();

		try {
			is = file.getInputStream();
			fileByteArray = IOUtils.toByteArray(is);
		} catch(Exception e) {
			throw new Exception("Could not create the bytearray: " + e.getMessage());
		} finally {
			IOUtils.closeQuietly(is);
		}

		fileToPersist.setFile(fileByteArray);
		fileToPersist.setFileType(file.getContentType());
		fileToPersist.setFileName(file.getOriginalFilename());

		dao.saveOrUpdate(fileToPersist);

		return fileToPersist;
	}

}
