package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.service.HttpProxyService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * Test of {@link HttpProxyController}
 *
 * @author Andre Henn
 * @author terrestris GmbH & co. KG
 */
public class HttpProxyControllerTest {

    private MockMvc mockMvc;

    @Mock(name = "httpProxyService")
    private HttpProxyService proxyService;

    private HttpProxyController httpProxyController;

    private final String PROXY_ENDPOINT = "/proxy.action";

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        httpProxyController = new HttpProxyController();
        httpProxyController.setProxyService(proxyService);

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup(httpProxyController).build();
    }

    @Test
    public void proxyShouldWorkAsExpected() throws Exception {
        String baseUrl = "https://terrestris.de";
        final String body = "TEST";
        ResponseEntity mockedResponse = new ResponseEntity(body, null, HttpStatus.OK);

        Mockito.when(proxyService.doProxy(
            Matchers.any(HttpServletRequest.class),
            Matchers.any(String.class),
            Matchers.any(Map.class))
        ).thenReturn(mockedResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(PROXY_ENDPOINT)
            .param("baseUrl", baseUrl))
            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertEquals("Returned body matched mocked one.", body, content);

        Mockito.verify(proxyService, Mockito.times(1)).
            doProxy(Matchers.any(HttpServletRequest.class),
                Matchers.any(String.class),
                Matchers.any(Map.class));
        Mockito.verifyNoMoreInteractions(proxyService);
    }
}
