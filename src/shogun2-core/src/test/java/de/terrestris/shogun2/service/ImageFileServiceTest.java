package de.terrestris.shogun2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import de.terrestris.shogun2.dao.ImageFileDao;
import de.terrestris.shogun2.model.ImageFile;

public class ImageFileServiceTest extends AbstractSecuredPersistentObjectServiceTest<ImageFile, ImageFileDao<ImageFile>, ImageFileService<ImageFile, ImageFileDao<ImageFile>>> {

	/**
	 *
	 * @throws Exception
	 */
	public void setUpImplToTest() throws Exception {
		implToTest = new ImageFile();
	}

	@Override
	protected ImageFileService<ImageFile, ImageFileDao<ImageFile>> getCrudService() {
		return new ImageFileService<ImageFile, ImageFileDao<ImageFile>>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<ImageFileDao<ImageFile>> getDaoClass() {
		return (Class<ImageFileDao<ImageFile>>) new ImageFileDao<ImageFile>().getClass();
	}

	@Test
	public void save_asExpected() throws Exception {

		ImageFile persistedImage = null;
		Integer dimension = 500;
		BufferedImage bimg = new BufferedImage(
				dimension,dimension,BufferedImage.TYPE_3BYTE_BGR);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( bimg, "jpg", baos );
		byte[] imageByteArray = baos.toByteArray();

		MockMultipartFile mockMultipartFile = new MockMultipartFile(
				"fileData",
				"fileName.jpg",
				"image/jpeg",
				imageByteArray);

		doNothing().when(dao).saveOrUpdate(any(ImageFile.class));


		persistedImage = crudService.saveImage(
				mockMultipartFile, false, dimension);

		verify(dao, times(1)).saveOrUpdate(any(ImageFile.class));
		verifyNoMoreInteractions(dao);

		assertTrue(Arrays.equals(persistedImage.getFile(),imageByteArray));
		assertTrue(Arrays.equals(persistedImage.getThumbnail(), null));
		assertEquals(persistedImage.getWidth(), dimension);
		assertEquals(persistedImage.getFileName(), "fileName.jpg");
		assertEquals(persistedImage.getFileType(), "image/jpeg");
	}

	@Test(expected=Exception.class)
	public void save_emptyFile() throws Exception {

		byte[] mockupBytes = null;

		MockMultipartFile emptyImage = new MockMultipartFile(
				"empty", mockupBytes);

		doThrow(new Exception("errormsg"))
			.when(dao).saveOrUpdate(any(ImageFile.class));

		crudService.saveImage(emptyImage, false, 0);

	}

	@Test
	public void get_asExpected() throws Exception {

		BufferedImage bimg = new BufferedImage(
				500,500,BufferedImage.TYPE_3BYTE_BGR);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( bimg, "jpg", baos );
		byte[] imageByteArray = baos.toByteArray();

		ImageFile persistedImage = null;

		MockMultipartFile mockMultipartFile = new MockMultipartFile(
			"fileData",
			"fileName.jpg",
			"image/jpeg",
			imageByteArray);

		doNothing().when(dao).saveOrUpdate(any(ImageFile.class));

		persistedImage = crudService.saveImage(mockMultipartFile, false, 0);

		Integer imageId = 998;

		when(dao.findById(imageId)).thenReturn(persistedImage);

		ImageFile retrievedImage = crudService.findById(imageId);

		verify(dao, times(1)).findById(imageId);

		assertTrue(Arrays.equals(retrievedImage.getFile(), imageByteArray));
		assertTrue(retrievedImage.getThumbnail() == null);
		assertEquals(retrievedImage.getFileName(), "fileName.jpg");
		assertEquals(retrievedImage.getFileType(), "image/jpeg");
	}

	@Test
	public void getThumbnail_asExpected() throws Exception {

		BufferedImage bimg = new BufferedImage(
				500,500,BufferedImage.TYPE_3BYTE_BGR);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( bimg, "jpg", baos );
		byte[] imageByteArray = baos.toByteArray();

		ImageFile persistedImage = null;

		MockMultipartFile mockMultipartFile = new MockMultipartFile(
			"fileData",
			"fileName.jpg",
			"image/jpeg",
			imageByteArray);

		byte[] thumbnail = ImageFileService.scaleImage(
				imageByteArray,
				"jpg",
				100);

		doNothing().when(dao).saveOrUpdate(any(ImageFile.class));

		persistedImage = crudService.saveImage(
				mockMultipartFile, true, 100);

		Integer imageId = 998;

		when(dao.findById(imageId)).thenReturn(persistedImage);

		ImageFile retrievedImage = crudService.findById(imageId);

		verify(dao, times(1)).findById(imageId);

		assertTrue(Arrays.equals(retrievedImage.getThumbnail(), thumbnail));
		assertEquals(retrievedImage.getFileName(), "fileName.jpg");
		assertEquals(retrievedImage.getFileType(), "image/jpeg");
	}

}
