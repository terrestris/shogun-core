package de.terrestris.shogun2.service;

import de.terrestris.shogun2.dao.FileDao;
import de.terrestris.shogun2.model.File;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FileServiceTest extends PermissionAwareCrudServiceTest<File, FileDao<File>, FileService<File, FileDao<File>>> {

    /**
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

    /**
     * @throws Exception
     */
    @Test
    public void upload_asExpected() throws Exception {

        byte[] mockupBytes = "Just some mockup bytes".getBytes();
        File persistedFile = null;

        final String fileName = "fileName.txt";
        final String fileType = "text/plain";

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "fileData",
            fileName,
            fileType,
            mockupBytes);

        doNothing().when(dao).saveOrUpdate(any(File.class));

        persistedFile = crudService.uploadFile(mockMultipartFile);

        verify(dao, times(1)).saveOrUpdate(any(File.class));
        verifyNoMoreInteractions(dao);

        assertEquals(new String(persistedFile.getFile()), new String(mockupBytes));
        assertEquals(persistedFile.getFileName(), fileName);
        assertEquals(persistedFile.getFileType(), fileType);
    }

    @Test(expected = Exception.class)
    public void upload_withError() throws Exception {
        final String fileName = "fileName.txt";
        final String fileType = "text/plain";

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "fileData",
                fileName,
                fileType,
                (byte[])null);

        doNothing().when(dao).saveOrUpdate(any(File.class));

        crudService.uploadFile(mockMultipartFile);

        verify(dao, times(1)).saveOrUpdate(any(File.class));
        verifyNoMoreInteractions(dao);
    }

    /**
     * @throws Exception
     */
    @Test(expected = Exception.class)
    public void upload_nullFile_shouldThrowException() throws Exception {
        crudService.uploadFile(null);
    }

    /**
     * @throws Exception
     */
    @Test(expected = Exception.class)
    public void upload_emptyFile_shouldThrowException() throws Exception {

        byte[] mockupBytes = null;

        MockMultipartFile emptyFile = new MockMultipartFile("empty", mockupBytes);

        crudService.uploadFile(emptyFile);

    }

}
