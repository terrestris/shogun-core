package de.terrestris.shogun2.web;

import de.terrestris.shogun2.service.TreeNodeService;
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
public class TreeNodeControllerTest {

    private MockMvc mockMvc;

    /**
     * The controller to test
     */
    private TreeNodeController treeNodeController;

    @Mock(name="treeNodeService")
    private TreeNodeService treeNodeService;

    @Before
    public void setUp() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        this.treeNodeController = new TreeNodeController();
        this.treeNodeController.setService(treeNodeService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(treeNodeController).build();
    }

    @Test
    public void findAllRequestMappings() throws Exception {
        // Perform and test the GET-Request
        mockMvc.perform(get("/treenodes"));
    }

}
