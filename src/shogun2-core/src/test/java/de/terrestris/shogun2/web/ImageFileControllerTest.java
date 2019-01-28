package de.terrestris.shogun2.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shogun2.model.ImageFile;
import de.terrestris.shogun2.service.ImageFileService;
import de.terrestris.shogun2.util.data.ResultSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link ImageFileController}
 *
 * @author Andre Henn
 */
public class ImageFileControllerTest {

    private final String GET_THUMBNAIL_ENDPOINT = "/image/getThumbnail.action";

    private MockMvc mockMvc;

    @Mock(name="imageFileService")
    private ImageFileService imageFileService;

    @Before
    public void setUp() throws Exception {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        ImageFileController imageFileController = new ImageFileController();
        imageFileController.setService(imageFileService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(imageFileController).build();
    }

    @Test
    public void getThumbnailReturnsErrorMapIfIdIsNull() throws Exception {
        Mockito.when(imageFileService.findById(Matchers.any(int.class))).thenReturn(null);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(GET_THUMBNAIL_ENDPOINT)
            .param("id", ""))
            .andExpect(status().isOk()).andReturn();

        assertEquals(result.getResponse().getStatus(), 200);
        ObjectMapper objectMapper = new ObjectMapper();
        assertEquals("Returned body matched mocked one.",
            objectMapper.writeValueAsString(ResultSet.error("Could not get the image thumbnail: Could not find the image with id null")), result.getResponse().getContentAsString());
    }

    @Test
    public void getThumbnailReturnsImage() throws Exception {
        ImageFile imgFile = new ImageFile();
        imgFile.setThumbnail(new byte[] {0, 8, 15});
        imgFile.setFileType("image/png");
        imgFile.setFileName("testImg");
        Mockito.when(imageFileService.findById(Matchers.any(int.class))).thenReturn(imgFile);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(GET_THUMBNAIL_ENDPOINT)
            .param("id", "121"))
            .andExpect(status().isOk()).andReturn();

        assertEquals(result.getResponse().getStatus(), 200);
    }
}
