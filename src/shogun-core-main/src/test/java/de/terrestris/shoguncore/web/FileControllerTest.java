package de.terrestris.shoguncore.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import de.terrestris.shoguncore.dao.FileDao;
import de.terrestris.shoguncore.model.File;
import de.terrestris.shoguncore.service.FileService;

/**
 * @author Nils Bühner
 */
public class FileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileService<File, FileDao<File>> fileServiceMock;

    /**
     * The controller to test
     */
    private FileController<File, FileDao<File>, FileService<File, FileDao<File>>> fileController;

    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // init the controller to test. this is necessary as InjectMocks
        // annotation will not work with the constructors of the controllers
        // (entityClass). see https://goo.gl/jLbMZe
        fileController = new FileController<File, FileDao<File>, FileService<File, FileDao<File>>>();
        fileController.setService(fileServiceMock);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(fileController)
            .build();
    }

    /**
     * @throws Exception
     */
    @Test
    public void uploadFile_shouldWorkAsExpected()
        throws Exception {

        // mock data
        byte[] mockupBytes = "Just some mockup bytes".getBytes();
        final String fileName = "fileName.txt";
        final String fileType = "text/plain";

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "file",
            fileName,
            fileType,
            mockupBytes);

        File uploadedFile = new File();
        uploadedFile.setFile(mockupBytes);
        uploadedFile.setFileName(fileName);
        uploadedFile.setFileType(fileType);

        // mock service behaviour
        when(fileServiceMock.uploadFile(mockMultipartFile)).thenReturn(uploadedFile);

        // Perform and test the GET-Request
        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/file/upload.action").file(mockMultipartFile))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.*", hasSize(3)))
            .andExpect(jsonPath("$.success", is(true)))
            .andExpect(jsonPath("$.total", is(1)))
            .andExpect(jsonPath("$.data.fileName", is(fileName)))
            .andExpect(jsonPath("$.data.fileType", is(fileType)));

        verify(fileServiceMock, times(1)).uploadFile(mockMultipartFile);
        verifyNoMoreInteractions(fileServiceMock);
    }

    /**
     * @throws Exception
     */
    @Test
    public void uploadFile_shouldReturnErrorMessageIfUploadIsEmpty()
        throws Exception {

        // mock service behaviour
        final String errorMessage = "Upload failed. File is null.";

        byte[] content = null;
        MockMultipartFile file = new MockMultipartFile("file", content);
        doThrow(new Exception(errorMessage))
            .when(fileServiceMock).uploadFile(file);

        // Perform and test the GET-Request
        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/file/upload.action").file(file))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.*", hasSize(2)))
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", containsString(errorMessage)));

        verify(fileServiceMock, times(1)).uploadFile(file);
        verifyNoMoreInteractions(fileServiceMock);
    }

    /**
     * @throws Exception
     */
    @Test
    public void getFile_shouldWorkAsExpected()
        throws Exception {

        // mock data
        final String fileContent = "Just some mockup bytes";
        byte[] mockupBytes = fileContent.getBytes();
        final String fileName = "fileName.txt";
        final String fileType = "text/plain";

        Integer fileId = 42;
        File expectedFile = new File();
        expectedFile.setFile(mockupBytes);
        expectedFile.setFileName(fileName);
        expectedFile.setFileType(fileType);

        // mock service behaviour
        when(fileServiceMock.findById(fileId)).thenReturn(expectedFile);

        // Perform and test the GET-Request
        mockMvc.perform(get("/file/get.action").param("id", fileId.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.parseMediaType(fileType)))
            .andExpect(content().string(fileContent));

        verify(fileServiceMock, times(1)).findById(fileId);
        verifyNoMoreInteractions(fileServiceMock);
    }

    /**
     * @throws Exception
     */
    @Test
    public void getFile_shouldReturnJsonErrorIfFileCouldNotBeFound()
        throws Exception {

        Integer fileId = 42;

        // mock service behaviour (-> file not found)
        when(fileServiceMock.findById(fileId)).thenReturn(null);

        // Perform and test the GET-Request
        mockMvc.perform(get("/file/get.action").param("id", fileId.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.*", hasSize(2)))
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", containsString("Could not find the file with id")));

        verify(fileServiceMock, times(1)).findById(fileId);
        verifyNoMoreInteractions(fileServiceMock);
    }

    /**
     * @throws Exception
     */
    @Test
    public void getFile_shouldReturnJsonErrorIfExceptionIsThrown()
        throws Exception {

        Integer fileId = 42;

        // mock service behaviour (exception)
        String errorMessage = "Some runtime exception";
        doThrow(new RuntimeException(errorMessage)).when(fileServiceMock).findById(fileId);

        // Perform and test the GET-Request
        mockMvc.perform(get("/file/get.action").param("id", fileId.toString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.*", hasSize(2)))
            .andExpect(jsonPath("$.success", is(false)))
            .andExpect(jsonPath("$.message", containsString(errorMessage)));

        verify(fileServiceMock, times(1)).findById(fileId);
        verifyNoMoreInteractions(fileServiceMock);
    }
}
