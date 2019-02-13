package de.terrestris.shogun2.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shogun2.service.MapService;
import de.terrestris.shogun2.util.data.ResultSet;
import org.junit.Assert;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for {@link MapController}
 *
 * @author Andre Henn
 */
public class MapControllerTest {

    final String SETLAYERS_ENDPOINT = "/maps/setLayersForMap.action";

    private MockMvc mockMvc;

    @Mock(name = "mapService")
    private MapService mapService;

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        MapController mapController = new MapController();
        mapController.setService(mapService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(mapController).build();
    }

    @Test
    public void setLayersForMapTest_erroneous() throws Exception {
        final List<Integer> layerIds = new ArrayList<>();
        layerIds.add(new Integer(1909));

        Mockito.when(mapService.setLayersForMap(
                Matchers.any(int.class),
                Matchers.any(List.class)
                )
        ).thenReturn(layerIds);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(SETLAYERS_ENDPOINT)
                .param("mapModuleId", "")
                .param("layerIds", ""))
                .andExpect(status().isOk()).andReturn();

        assertEquals(result.getResponse().getStatus(), 200);

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Assert.assertEquals("Returned body matched mocked one.",
            objectMapper.writeValueAsString(ResultSet.error(MapController.COULD_NOT_SET_ERROR_MSG)), content);

        // should not be called since mapModuleId and layerIds are empty
        Mockito.verify(mapService, Mockito.times(0)).
            setLayersForMap(
                Matchers.any(int.class),
                Matchers.any(List.class)
            );
        Mockito.verifyNoMoreInteractions(mapService);
    }

    @Test
    public void setLayersForMapTest_ok() throws Exception {
        final List<Integer> layerIds = new ArrayList<>();
        layerIds.add(new Integer(1909));

        Mockito.when(mapService.setLayersForMap(
            Matchers.any(int.class),
            Matchers.anyList()
            )
        ).thenReturn(layerIds);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(SETLAYERS_ENDPOINT)
                .param("mapModuleId", "1909")
                .param("layerIds", "0815")
                .param("layerIds", "4711"))
                .andExpect(status().isOk()).andReturn();

        String content = result.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        Assert.assertEquals("Returned body matched mocked one.",
                objectMapper.writeValueAsString(ResultSet.success(layerIds)), content);

        Mockito.verify(mapService, Mockito.times(1)).
                setLayersForMap(
                    Matchers.any(int.class),
                    Matchers.any(List.class)
                );
        Mockito.verifyNoMoreInteractions(mapService);
    }
}
