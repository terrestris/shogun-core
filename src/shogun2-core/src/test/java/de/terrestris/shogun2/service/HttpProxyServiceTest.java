package de.terrestris.shogun2.service;

import de.terrestris.shogun2.util.http.HttpUtil;
import de.terrestris.shogun2.util.model.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * Tests for {@link HttpProxyService}
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpUtil.class)
@PowerMockIgnore({"javax.management.*", "org.mockito.*", "org.powermock.*", "org.apache.commons.*", "org.junit.*", "javax.xml.*", "org.xml.*"})
public class HttpProxyServiceTest {

    @InjectMocks
    private HttpProxyService proxyService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        final List<String> whiteList = new LinkedList<>();
        // only domainName will be checked in proxy
        whiteList.add("terrestris.de");
        whiteList.add("terrestris.de:443");
        whiteList.add("mundialis.de");
        this.proxyService.setProxyWhiteList(whiteList);
    }

    @Test
    public void proxy_returns_400_when_no_url_is_given() {
        final ResponseEntity responseEntity = proxyService.doProxy(null, null, null);
        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_400_NO_URL, responseEntity.getBody());
    }

    @Test
    public void proxy_returns_400_when_baseUrl_is_empty() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        final String baseUrl = StringUtils.EMPTY;
        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_400_NO_URL, responseEntity.getBody());
    }

    @Test
    public void proxy_returns_500_when_baseUrl_is_no_valid_URL() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        final String baseUrl = "$$$$___S04___$$$$";
        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Assert.assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_500, responseEntity.getBody());
    }

    @Test
    public void proxy_returns_502_when_baseUrl_is_not_in_URL_whitelist() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        final String baseUrl = "https://unallowedHost.com/unallowedPath";

        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_GATEWAY, responseEntity.getStatusCode());
        Assert.assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_502, responseEntity.getBody());
    }

    @Test
    public void proxy_returns_405_for_unsupported_HTTP_methods() {
        // currently unsupported
        final HttpMethod[] unsupportedHttpMethods = new HttpMethod[]{
            HttpMethod.DELETE,
            HttpMethod.PUT,
            HttpMethod.HEAD,
            HttpMethod.PATCH,
            HttpMethod.TRACE,
            HttpMethod.OPTIONS
        };
        final String baseUrl = "https://www.terrestris.de/internet.txt";

        PowerMockito.mockStatic(HttpUtil.class);
        for (HttpMethod unsupportedHttpMethod : unsupportedHttpMethods) {
            HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
            Mockito.when(mockedRequest.getMethod()).thenReturn(unsupportedHttpMethod.name());
            final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
            Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
            Assert.assertEquals("Returned Status message matched", HttpProxyService.ERR_MSG_405, responseEntity.getBody());
        }
    }

    @Test
    public void proxy_returns_200_for_allowed_GET_request() throws URISyntaxException, HttpException {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        PowerMockito.mockStatic(HttpUtil.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/internet.txt";
        final URI baseUri = new URI(baseUrl);
        Response mockedRespone = Mockito.mock(Response.class);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(true);
        Mockito.when(HttpUtil.forwardGet(baseUri, mockedRequest, false)).thenReturn(mockedRespone);

        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.OK, responseEntity.getStatusCode());

        final byte[] responseBodyAsByteArray = (byte[]) responseEntity.getBody();
        Assert.assertEquals("Returned content matched mocked one.", new String(responseBodyAsByteArray), internetContent);
    }

    @Test
    public void proxy_returns_400_for_erroneous_GET_requests() throws URISyntaxException, HttpException {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        PowerMockito.mockStatic(HttpUtil.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/internet.txt";
        final URI baseUri = new URI(baseUrl);
        Response mockedRespone = Mockito.mock(Response.class);
        final String msg = "ERROR";

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(true);
        Mockito.when(HttpUtil.forwardGet(baseUri, mockedRequest, false)).thenThrow(new HttpException(msg));

        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("Returned Status matched", HttpProxyService.ERR_MSG_400_COMMON, responseEntity.getBody());
    }

    @Test
    public void proxy_returns_200_for_allowed_POST_request() throws URISyntaxException, HttpException {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        PowerMockito.mockStatic(HttpUtil.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/endpointToPostAt";
        final URI baseUri = new URI(baseUrl);
        Response mockedRespone = Mockito.mock(Response.class);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
        Mockito.when(HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
        Mockito.when(HttpUtil.forwardPost(baseUri, mockedRequest, false)).thenReturn(mockedRespone);

        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.OK, responseEntity.getStatusCode());

        final byte[] responseBodyAsByteArray = (byte[]) responseEntity.getBody();
        Assert.assertEquals("Returned content matched mocked one.", new String(responseBodyAsByteArray), internetContent);
    }

    @Test
    public void proxy_returns_400_for_erroneous_POST_requests() throws URISyntaxException, HttpException {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        PowerMockito.mockStatic(HttpUtil.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/internet.txt";
        final URI baseUri = new URI(baseUrl);
        Response mockedRespone = Mockito.mock(Response.class);
        final String msg = "ERROR";

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
        Mockito.when(HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
        Mockito.when(HttpUtil.forwardPost(baseUri, mockedRequest, false)).thenThrow(new HttpException(msg));

        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("Returned Status matched", HttpProxyService.ERR_MSG_400_COMMON, responseEntity.getBody());
    }


    @Test
    public void proxy_returns_200_for_allowed_FormMultipartPost_request() throws URISyntaxException, HttpException, IOException, ServletException {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        PowerMockito.mockStatic(HttpUtil.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/endpointToPostAt";
        final URI baseUri = new URI(baseUrl);
        Response mockedRespone = Mockito.mock(Response.class);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
        Mockito.when(HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
        Mockito.when(HttpUtil.isFormMultipartPost(mockedRequest)).thenReturn(true);
        Mockito.when(HttpUtil.forwardFormMultipartPost(baseUri, mockedRequest, false)).thenReturn(mockedRespone);

        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.OK, responseEntity.getStatusCode());

        final byte[] responseBodyAsByteArray = (byte[]) responseEntity.getBody();
        Assert.assertEquals("Returned content matched mocked one.", new String(responseBodyAsByteArray), internetContent);
    }

    @Test
    public void proxy_returns_400_for_erroneous_FormMultipartPost_requests() throws URISyntaxException, HttpException, IOException, ServletException {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        PowerMockito.mockStatic(HttpUtil.class);
        final String internetContent = "THE INTERNET!";
        final String baseUrl = "https://www.terrestris.de/internet.txt";
        final URI baseUri = new URI(baseUrl);
        Response mockedRespone = Mockito.mock(Response.class);
        final String msg = "ERROR";

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        HttpStatus status = HttpStatus.OK;

        Mockito.when(mockedRespone.getHeaders()).thenReturn(headers);
        Mockito.when(mockedRespone.getBody()).thenReturn(internetContent.getBytes());
        Mockito.when(mockedRespone.getStatusCode()).thenReturn(status);
        Mockito.when(HttpUtil.isHttpGetRequest(mockedRequest)).thenReturn(false);
        Mockito.when(HttpUtil.isHttpPostRequest(mockedRequest)).thenReturn(true);
        Mockito.when(HttpUtil.isFormMultipartPost(mockedRequest)).thenReturn(true);
        Mockito.when(HttpUtil.forwardFormMultipartPost(baseUri, mockedRequest, false)).thenThrow(new HttpException(msg));

        final ResponseEntity responseEntity = proxyService.doProxy(mockedRequest, baseUrl, null);
        Assert.assertEquals("Returned Status code matched mocked one.", HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("Returned Status matched", HttpProxyService.ERR_MSG_400_COMMON, responseEntity.getBody());
    }
}
