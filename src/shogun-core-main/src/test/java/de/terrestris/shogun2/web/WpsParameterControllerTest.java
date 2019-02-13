package de.terrestris.shogun2.web;

import de.terrestris.shogun2.service.WpsParameterService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Test class for {@link WpsParameterController}
 *
 * @author Andre Henn
 */
public class WpsParameterControllerTest {

    private MockMvc mockMvc;

    /**
     * The controller to test
     */
    private WpsParameterController wpsParameterController;

    @Mock(name="wpsParameterService")
    private WpsParameterService wpsParameterService;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        this.wpsParameterController = new WpsParameterController();
        this.wpsParameterController.setService(wpsParameterService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(wpsParameterController).build();
    }

    @Test
    public void findAllRequestMappings() throws Exception {
        // Perform and test the GET-Request
        mockMvc.perform(get("/wpsparameters"));
    }

}
