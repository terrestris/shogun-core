package de.terrestris.shogun2.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import de.terrestris.shogun2.dao.InterceptorRuleDao;
import de.terrestris.shogun2.model.interceptor.InterceptorRule;
import de.terrestris.shogun2.util.http.HttpUtil;
import de.terrestris.shogun2.util.interceptor.InterceptorException;
import de.terrestris.shogun2.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shogun2.util.interceptor.OgcMessage;
import de.terrestris.shogun2.util.interceptor.OgcMessageDistributor;
import de.terrestris.shogun2.util.model.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpUtil.class)
public class GeoServerInterceptorServiceTest {

	@InjectMocks
	GeoServerInterceptorService gsInterceptorService;

	@Mock(name="interceptorRuleService")
	InterceptorRuleService<InterceptorRule, InterceptorRuleDao<InterceptorRule>> ruleService;

	@Mock
	OgcMessageDistributor ogcMessageDistributor;

	@Before
	public void setUp() throws IOException {
		MockitoAnnotations.initMocks(this);

		Properties geoServerNameSpaces = new Properties();
		geoServerNameSpaces.setProperty("topp", "http://localhost:1234/geoserver/topp/ows");
		gsInterceptorService.setGeoServerNameSpaces(geoServerNameSpaces);
	}

	@Test(expected=InterceptorException.class)
	public void test_throws_on_non_ogc_request() throws InterceptorException,
	URISyntaxException, HttpException, IOException {
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		gsInterceptorService.interceptGeoServerRequest(httpRequest);
	}

	@Test
	public void send_get() throws InterceptorException,
			URISyntaxException, HttpException, IOException {

		Response resp = new Response();

		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setRequestURI("http://example.com/geoserver.action");
		httpRequest.setParameter("SERVICE", "WMS");
		httpRequest.setParameter("REQUEST", "GetMap");
		httpRequest.setParameter("LAYERS", "topp:maul");
		httpRequest.setMethod("GET");

		PowerMockito.mockStatic(HttpUtil.class);
		when(HttpUtil.get(any(String.class))).thenReturn(resp);

		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);

		when(ogcMessageDistributor.distributeToRequestInterceptor(
				any(MutableHttpServletRequest.class), any(OgcMessage.class))).thenReturn(mutableRequest);

		when(ogcMessageDistributor.distributeToResponseInterceptor(
				any(Response.class), any(OgcMessage.class))).thenReturn(resp);

		Response got = gsInterceptorService.interceptGeoServerRequest(httpRequest);

		assertEquals(resp, got);

	}

	@Test
	public void send_post_kvp() throws URISyntaxException, HttpException,
			InterceptorException, IOException {

		Response resp = new Response();

		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setRequestURI("http://example.com/geoserver.action");
		httpRequest.setParameter("SERVICE", "WMS");
		httpRequest.setParameter("REQUEST", "GetMap");
		httpRequest.setParameter("LAYERS", "topp:maul");
		httpRequest.setMethod("POST");

		PowerMockito.mockStatic(HttpUtil.class);
		when(HttpUtil.post(any(String.class), any(List.class))).thenReturn(resp);

		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);

		when(ogcMessageDistributor.distributeToRequestInterceptor(
				any(MutableHttpServletRequest.class), any(OgcMessage.class))).thenReturn(mutableRequest);

		when(ogcMessageDistributor.distributeToResponseInterceptor(
				any(Response.class), any(OgcMessage.class))).thenReturn(resp);

		Response got = gsInterceptorService.interceptGeoServerRequest(httpRequest);

		assertEquals(resp, got);

	}

	@Test
	public void send_post_body() throws URISyntaxException, HttpException,
			InterceptorException, IOException {

		Response resp = new Response();

		String describeFeature =
				"<DescribeFeatureType " +
				"  version=\"1.1.0\" " +
				"  service=\"WFS\" " +
				"  xmlns=\"http://www.opengis.net/wfs\" " +
				"  xmlns:topp=\"http://www.openplans.org/topp\" " +
				"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
				"  xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd\"> " +
				"    <TypeName>topp:maul</TypeName> " +
				"</DescribeFeatureType>";

		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setRequestURI("http://example.com/geoserver.action");
		httpRequest.setContent(describeFeature.getBytes());
		httpRequest.setContentType(ContentType.APPLICATION_XML.toString());
		httpRequest.setMethod("POST");

		PowerMockito.mockStatic(HttpUtil.class);
		when(HttpUtil.post(any(String.class), any(String.class), any(ContentType.class))).thenReturn(resp);

		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);

		when(ogcMessageDistributor.distributeToRequestInterceptor(
				any(MutableHttpServletRequest.class), any(OgcMessage.class))).thenReturn(mutableRequest);

		when(ogcMessageDistributor.distributeToResponseInterceptor(
				any(Response.class), any(OgcMessage.class))).thenReturn(resp);

		Response got = gsInterceptorService.interceptGeoServerRequest(httpRequest);

		assertEquals(resp, got);

	}

}
