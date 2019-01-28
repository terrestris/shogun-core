package de.terrestris.shogun2.web;

import de.terrestris.shogun2.service.WpsParameterService;
import de.terrestris.shogun2.service.WpsPluginService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Test class for {@link WpsPluginController}
 *
 * @author Andre Henn
 */
public class WpsPluginControllerTest {

    private MockMvc mockMvc;

    /**
     * The controller to test
     */
    private WpsPluginController wpsPluginController;

    @Mock(name="wpsPluginService")
    private WpsPluginService wpsPluginService;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        this.wpsPluginController = new WpsPluginController();
        this.wpsPluginController.setService(wpsPluginService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(wpsPluginController).build();
    }

    @Test
    public void findAllRequestMappings() throws Exception {
        // Perform and test the GET-Request
        mockMvc.perform(get("/wpsplugins"));
    }

}
