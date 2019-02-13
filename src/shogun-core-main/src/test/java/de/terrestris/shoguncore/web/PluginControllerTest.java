package de.terrestris.shoguncore.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shoguncore.service.PluginService;
import de.terrestris.shoguncore.util.data.ResultSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for {@link PluginController}
 *
 * @author Andre Henn
 */
public class PluginControllerTest {

    private MockMvc mockMvc;

    @Mock(name = "pluginService")
    private PluginService pluginService;

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        PluginController pluginController = new PluginController();
        pluginController.setService(pluginService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(pluginController).build();
    }

    @Test
    public void preCheckDelete() throws Exception {
        final String resultMsg = "Everything fine.";
        final List<String> expectedResult = new ArrayList<>();
        expectedResult.add(resultMsg);

        Mockito.when(pluginService.preCheckDelete(
            Matchers.any(Integer.class))
        ).thenReturn(expectedResult);

        String PRECHECKDELETE_ENDPOINT = "/plugins/preCheckDelete.action";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(PRECHECKDELETE_ENDPOINT)
            .param("pluginId", "1909")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Assert.assertEquals("Returned body matched mocked one.",
            objectMapper.writeValueAsString(ResultSet.success(expectedResult)), content);

        Mockito.verify(pluginService, Mockito.times(1)).
            preCheckDelete(Matchers.any(Integer.class));
        Mockito.verifyNoMoreInteractions(pluginService);
    }

}
