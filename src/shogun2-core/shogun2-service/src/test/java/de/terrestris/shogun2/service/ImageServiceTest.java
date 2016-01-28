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

import de.terrestris.shogun2.dao.ImageDao;
import de.terrestris.shogun2.model.Image;

public class ImageServiceTest extends AbstractExtDirectCrudServiceTest<Image, ImageDao<Image>, ImageService<Image, ImageDao<Image>>> {

	/**
	 *
	 * @throws Exception
	 */
	public void setUpImplToTest() throws Exception {
		implToTest = new Image();
	}

	@Override
	protected ImageService<Image, ImageDao<Image>> getCrudService() {
		return new ImageService<Image, ImageDao<Image>>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<ImageDao<Image>> getDaoClass() {
		return (Class<ImageDao<Image>>) new ImageDao<Image>().getClass();
	}

	@Test
	public void upload_asExpected() throws Exception {

		Image persistedImage = null;

		BufferedImage bimg = new BufferedImage(
				500,500,BufferedImage.TYPE_3BYTE_BGR);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( bimg, "jpg", baos );
		byte[] imageByteArray = baos.toByteArray();

		MockMultipartFile mockMultipartFile = new MockMultipartFile(
				"fileData",
				"fileName.jpg",
				"image/jpeg",
				imageByteArray);

		byte[] thumbnail = ImageService.scaleImage(
				imageByteArray,
				"jpg",
				100);

		doNothing().when(dao).saveOrUpdate(any(Image.class));

		persistedImage = crudService.uploadImage(mockMultipartFile, true, 100);

		verify(dao, times(1)).saveOrUpdate(any(Image.class));
		verifyNoMoreInteractions(dao);

		assertTrue(Arrays.equals(persistedImage.getFile(),imageByteArray));
		assertTrue(Arrays.equals(persistedImage.getThumbnail(), thumbnail));
		assertEquals(persistedImage.getFileName(), "fileName.jpg");
		assertEquals(persistedImage.getFileType(), "image/jpeg");
	}

	@Test(expected=Exception.class)
	public void upload_emptyFile() throws Exception {

		byte[] mockupBytes = null;

		MockMultipartFile emptyImage = new MockMultipartFile(
				"empty", mockupBytes);

		doThrow(new Exception("errormsg"))
			.when(dao).saveOrUpdate(any(Image.class));

		crudService.uploadImage(emptyImage, false, 0);

	}

	@Test
	public void get_asExpected() throws Exception {

		BufferedImage bimg = new BufferedImage(
				500,500,BufferedImage.TYPE_3BYTE_BGR);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( bimg, "jpg", baos );
		byte[] imageByteArray = baos.toByteArray();

		Image persistedImage = null;

		MockMultipartFile mockMultipartFile = new MockMultipartFile(
			"fileData",
			"fileName.jpg",
			"image/jpeg",
			imageByteArray);

		doNothing().when(dao).saveOrUpdate(any(Image.class));

		persistedImage = crudService.uploadImage(mockMultipartFile, false, 0);

		Integer imageId = 998;

		when(dao.findById(imageId)).thenReturn(persistedImage);

		Image retrievedImage = crudService.getImage(imageId);

		verify(dao, times(1)).findById(imageId);

		assertTrue(Arrays.equals(retrievedImage.getFile(), imageByteArray));
		assertTrue(retrievedImage.getThumbnail() == null);
		assertEquals(retrievedImage.getFileName(), "fileName.jpg");
		assertEquals(retrievedImage.getFileType(), "image/jpeg");
	}

	@Test(expected=Exception.class)
	public void get_notFound() throws Exception {

		Integer id = 999;

		when(crudService.findById(999)).thenReturn(null);

		Image retrievedImage = crudService.getImage(id);

		verify(crudService, times(1)).getImage(id);
		verifyNoMoreInteractions(crudService);

		assertEquals(retrievedImage, null);
	}

}
