package de.terrestris.shoguncore.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import de.terrestris.shoguncore.service.EndpointDocService;

/**
 * @author Christian Mayer
 */
public class EndpointDocControllerTest {

    private MockMvc mockMvc;

    /**
     * The controller to test
     */
    private EndpointDocController endpointDocController;

    @Mock
    private EndpointDocService endpointDocServiceMock;

    @Mock
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // init the controller to test. this is necessary as InjectMocks
        // annotation will not work with the constructors of the controllers
        // (entityClass). see https://goo.gl/jLbMZe
        endpointDocController = new EndpointDocController();
        endpointDocController.setService(endpointDocServiceMock);
        endpointDocController.setRequestMappingHandlerMapping(requestMappingHandlerMapping);
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(endpointDocController).build();
    }

    @Test
    public void findAllRequestMappings_shouldReturnAllRequestMappings() throws Exception {

        // Perform and test the GET-Request
        mockMvc.perform(get("/endpointdoc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8"))
            .andExpect(content().string("[]"));
    }
}
