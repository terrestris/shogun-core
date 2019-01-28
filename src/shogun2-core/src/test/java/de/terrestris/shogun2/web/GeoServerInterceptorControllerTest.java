package de.terrestris.shogun2.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.terrestris.shogun2.service.GeoServerInterceptorService;
import de.terrestris.shogun2.util.data.ResultSet;
import de.terrestris.shogun2.util.interceptor.InterceptorException;
import de.terrestris.shogun2.util.model.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URISyntaxException;

import static de.terrestris.shogun2.web.GeoServerInterceptorController.ERROR_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link GeoServerInterceptorController}
 */
public class GeoServerInterceptorControllerTest {

    private final String INTERCEPTOR_ENDPOINT = "/geoserver.action";

    private MockMvc mockMvc;

    @Mock(name = "geoServerInterceptorService")
    private GeoServerInterceptorService geoServerInterceptorService;

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        GeoServerInterceptorController geoServerInterceptorController = new GeoServerInterceptorController();
        geoServerInterceptorController.setService(geoServerInterceptorService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(geoServerInterceptorController).build();
    }

    @Test
    public void worksForHttpGet() throws Exception {
        final String testString = "test";
        HttpHeaders responseHeaders = new HttpHeaders();
        Response responseObject = new Response(HttpStatus.OK, responseHeaders, testString.getBytes());

        Mockito.when(geoServerInterceptorService.interceptGeoServerRequest(
            Matchers.any(HttpServletRequest.class)
        )).thenReturn(responseObject);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(INTERCEPTOR_ENDPOINT))
            .andExpect(status().isOk()).andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
        assertEquals(result.getResponse().getContentAsString(), testString);
    }

    @Test
    public void worksForHttpPost() throws Exception {
        final String testString = "test";
        HttpHeaders responseHeaders = new HttpHeaders();
        Response responseObject = new Response(HttpStatus.OK, responseHeaders, testString.getBytes());

        Mockito.when(geoServerInterceptorService.interceptGeoServerRequest(
            Matchers.any(HttpServletRequest.class)
        )).thenReturn(responseObject);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(INTERCEPTOR_ENDPOINT))
            .andExpect(status().isOk()).andReturn();

        assertEquals(result.getResponse().getStatus(), HttpStatus.OK.value());
        assertEquals(result.getResponse().getContentAsString(), testString);
    }

    @Test
    public void doesntWorkForHttpPut() throws Exception {
        final String testString = "test";
        HttpHeaders responseHeaders = new HttpHeaders();
        Response responseObject = new Response(HttpStatus.OK, responseHeaders, testString.getBytes());

        Mockito.when(geoServerInterceptorService.interceptGeoServerRequest(
            Matchers.any(HttpServletRequest.class)
        )).thenReturn(responseObject);

        /**
         * return method not allowed
         */
        mockMvc.perform(MockMvcRequestBuilders.put(INTERCEPTOR_ENDPOINT))
            .andExpect(status().isMethodNotAllowed()).andReturn();
    }

    @Test
    public void returnsErrorObjectIfExceptionWasThrown() throws Exception {
        Mockito.when(geoServerInterceptorService.interceptGeoServerRequest(
            Matchers.any(HttpServletRequest.class)
        )).thenThrow(InterceptorException.class);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(INTERCEPTOR_ENDPOINT))
            .andExpect(status().isOk()).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        assertEquals("Returned body matched mocked one.",
            objectMapper.writeValueAsString(ResultSet.error(ERROR_MESSAGE+"null")), result.getResponse().getContentAsString());
    }

}
