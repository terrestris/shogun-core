package de.terrestris.shogun2.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import de.terrestris.shogun2.dao.FileDao;
import de.terrestris.shogun2.model.File;

public class FileServiceTest extends AbstractExtDirectCrudServiceTest<File, FileDao<File>, FileService<File, FileDao<File>>> {

	/**
	 *
	 * @throws Exception
	 */
	public void setUpImplToTest() throws Exception {
		implToTest = new File();
	}

	@Override
	protected FileService<File, FileDao<File>> getCrudService() {
		return new FileService<File, FileDao<File>>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<FileDao<File>> getDaoClass() {
		return (Class<FileDao<File>>) new FileDao<File>().getClass();
	}

	@Test
	public void upload_asExpected() throws Exception {

		byte[] mockupBytes = "Just some mockup bytes".getBytes();
		File persistedFile = null;

		MockMultipartFile mockMultipartFile = new MockMultipartFile(
			"fileData",
			"fileName.txt",
			"text/plain",
			mockupBytes);

		doNothing().when(dao).saveOrUpdate(any(File.class));

		persistedFile = crudService.uploadFile(mockMultipartFile);

		verify(dao, times(1)).saveOrUpdate(any(File.class));
		verifyNoMoreInteractions(dao);

		assertEquals(new String(persistedFile.getFile()), new String(mockupBytes));
		assertEquals(persistedFile.getFileName(), "fileName.txt");
		assertEquals(persistedFile.getFileType(), "text/plain");
	}

	@Test(expected=Exception.class)
	public void upload_emptyFile() throws Exception {

		byte[] mockupBytes = null;

		MockMultipartFile emptyFile = new MockMultipartFile("empty", mockupBytes);

		doThrow(new Exception("errormsg"))
			.when(dao).saveOrUpdate(any(File.class));

		crudService.uploadFile(emptyFile);

	}

	@Test
	public void get_asExpected() throws Exception {

		byte[] mockupBytes = "Just some mockup bytes".getBytes();
		File persistedFile = null;

		MockMultipartFile mockMultipartFile = new MockMultipartFile(
			"fileData",
			"fileName.txt",
			"text/plain",
			mockupBytes);

		doNothing().when(dao).saveOrUpdate(any(File.class));

		persistedFile = crudService.uploadFile(mockMultipartFile);

		Integer fileId = persistedFile.getId();

		when(crudService.getFile(fileId)).thenReturn(persistedFile);

		File retrievedFile = crudService.getFile(fileId);

		assertEquals(new String(retrievedFile.getFile()), new String(mockupBytes));
		assertEquals(retrievedFile.getFileName(), "fileName.txt");
		assertEquals(retrievedFile.getFileType(), "text/plain");
	}

	@Test(expected=Exception.class)
	public void get_notFound() throws Exception {

		Integer id = 999;

		when(crudService.findById(999)).thenReturn(null);

		File retrievedFile = crudService.getFile(id);

		verify(crudService, times(1)).getFile(id);
		verifyNoMoreInteractions(crudService);

		assertEquals(retrievedFile, null);
	}

}
