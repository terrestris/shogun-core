package de.terrestris.shogun2.web;

import de.terrestris.shogun2.service.WpsPrimitiveService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * TODO: add documentation
 *
 * @author Andre Henn
 */
public class WpsPrimitiveControllerTest {

    private MockMvc mockMvc;

    /**
     * The controller to test
     */
    private WpsPrimitiveController wpsPrimitiveController;

    @Mock(name="wpsPrimitiveService")
    private WpsPrimitiveService wpsPrimitiveService;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        this.wpsPrimitiveController = new WpsPrimitiveController();
        this.wpsPrimitiveController.setService(wpsPrimitiveService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(wpsPrimitiveController).build();
    }

    @Test
    public void findAllRequestMappings() throws Exception {
        // Perform and test the GET-Request
        mockMvc.perform(get("/wpsprimitives"));
    }

}
