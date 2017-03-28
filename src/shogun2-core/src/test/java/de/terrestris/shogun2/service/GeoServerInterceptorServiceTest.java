package de.terrestris.shogun2.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
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
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;

import de.terrestris.shogun2.dao.InterceptorRuleDao;
import de.terrestris.shogun2.model.interceptor.InterceptorRule;
import de.terrestris.shogun2.util.enumeration.HttpEnum;
import de.terrestris.shogun2.util.enumeration.InterceptorEnum;
import de.terrestris.shogun2.util.enumeration.OgcEnum;
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
		geoServerNameSpaces.setProperty("bvb", "http://localhost:1234/geoserver/bvb/ows");
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
		httpRequest.setParameter("LAYERS", "bvb:shinji");
		httpRequest.setMethod("GET");

		PowerMockito.mockStatic(HttpUtil.class);
		when(HttpUtil.get(any(String.class))).thenReturn(resp);

		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);

		when(ogcMessageDistributor.distributeToRequestInterceptor(
				any(MutableHttpServletRequest.class), any(OgcMessage.class))).thenReturn(mutableRequest);

		when(ogcMessageDistributor.distributeToResponseInterceptor(
				any(MutableHttpServletRequest.class), any(Response.class), any(OgcMessage.class))).thenReturn(resp);

		when(ruleService.findAllRulesForServiceAndEvent(
				any(String.class), any(String.class))).thenReturn(
				getTestInterceptorRulesForServiceAndEvent("WMS", "REQUEST"));

		Response got = gsInterceptorService.interceptGeoServerRequest(httpRequest);

		assertEquals(resp, got);

	}

	@Test
	@SuppressWarnings("unchecked")
	public void send_post_kvp() throws URISyntaxException, HttpException,
			InterceptorException, IOException {

		Response resp = new Response();

		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setRequestURI("http://example.com/geoserver.action");
		httpRequest.setParameter("SERVICE", "WMS");
		httpRequest.setParameter("REQUEST", "GetMap");
		httpRequest.setParameter("LAYERS", "bvb:shinji");
		httpRequest.setMethod("POST");

		PowerMockito.mockStatic(HttpUtil.class);
		when(HttpUtil.post(any(String.class), any(List.class))).thenReturn(resp);

		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);

		when(ogcMessageDistributor.distributeToRequestInterceptor(
				any(MutableHttpServletRequest.class), any(OgcMessage.class))).thenReturn(mutableRequest);

		when(ogcMessageDistributor.distributeToResponseInterceptor(
				any(MutableHttpServletRequest.class), any(Response.class), any(OgcMessage.class))).thenReturn(resp);

		when(ruleService.findAllRulesForServiceAndEvent(
				any(String.class), any(String.class))).thenReturn(
				getTestInterceptorRulesForServiceAndEvent("WMS", "REQUEST"));

		Response got = gsInterceptorService.interceptGeoServerRequest(httpRequest);

		assertEquals(resp, got);

	}

	@Test
	@SuppressWarnings("unchecked")
	public void send_post_kvp_and_query_params() throws URISyntaxException, HttpException, InterceptorException, IOException {
		Response resp = new Response();

		String url = "http://example.com/geoserver.action";
		String queryString = "FC=effzeh&HUMPTY=dumpty";
		String expectedPostUrl = url + "?" + queryString;

		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setRequestURI(url); // Set the raw URL …
		httpRequest.setQueryString(queryString); // and the query string …
		httpRequest.setParameter("SERVICE", "WMS");
		httpRequest.setParameter("REQUEST", "GetMap");
		httpRequest.setParameter("LAYERS", "bvb:shinji");
		httpRequest.setMethod("POST");

		PowerMockito.mockStatic(HttpUtil.class);
		when(
			// …but only return the created resp when the expected URL is requested
			HttpUtil.post(eq(expectedPostUrl), any(List.class))
		).thenReturn(resp);

		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);

		when(ogcMessageDistributor.distributeToRequestInterceptor(
				any(MutableHttpServletRequest.class), any(OgcMessage.class))).thenReturn(mutableRequest);

		// for the next stub it would be even better if we could use .then(returnsFirstArg() but this needs java 8
		when(ogcMessageDistributor.distributeToResponseInterceptor(
				any(MutableHttpServletRequest.class), any(Response.class), any(OgcMessage.class))).thenReturn(resp);

		when(ruleService.findAllRulesForServiceAndEvent(
				any(String.class), any(String.class))).thenReturn(
				getTestInterceptorRulesForServiceAndEvent("WMS", "REQUEST"));

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
				"  xmlns:bvb=\"http://www.openplans.org/bvb\" " +
				"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
				"  xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd\"> " +
				"    <TypeName>bvb:shinji</TypeName> " +
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

		// for the next stub it would be even better if we could use .then(returnsFirstArg() but this needs java 8
		when(ogcMessageDistributor.distributeToResponseInterceptor(
				any(MutableHttpServletRequest.class), any(Response.class), any(OgcMessage.class))).thenReturn(resp);

		when(ruleService.findAllRulesForServiceAndEvent(
				any(String.class), any(String.class))).thenReturn(
						getTestInterceptorRulesForServiceAndEvent("WFS", "REQUEST"));

		Response got = gsInterceptorService.interceptGeoServerRequest(httpRequest);

		assertEquals(resp, got);
	}

	@Test
	public void send_post_body_and_query_params() throws URISyntaxException, HttpException,
			InterceptorException, IOException {

		String url = "http://example.com/geoserver.action";
		String queryString = "FC=effzeh&HUMPTY=dumpty";
		String expectedPostUrl = url + "?" + queryString;

		Response resp = new Response();

		String describeFeature =
				"<DescribeFeatureType " +
				"  version=\"1.1.0\" " +
				"  service=\"WFS\" " +
				"  xmlns=\"http://www.opengis.net/wfs\" " +
				"  xmlns:bvb=\"http://www.openplans.org/bvb\" " +
				"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
				"  xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.1.0/wfs.xsd\"> " +
				"    <TypeName>bvb:shinji</TypeName> " +
				"</DescribeFeatureType>";

		MockHttpServletRequest httpRequest = new MockHttpServletRequest();
		httpRequest.setRequestURI(url); // Set the raw URL …
		httpRequest.setQueryString(queryString); // and the query string …
		httpRequest.setContent(describeFeature.getBytes());
		httpRequest.setContentType(ContentType.APPLICATION_XML.toString());
		httpRequest.setMethod("POST");

		PowerMockito.mockStatic(HttpUtil.class);
		when(
			// …but only return the created resp when the expected URL is requested
			HttpUtil.post(eq(expectedPostUrl), any(String.class), any(ContentType.class))
		).thenReturn(resp);

		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);

		when(ogcMessageDistributor.distributeToRequestInterceptor(
				any(MutableHttpServletRequest.class), any(OgcMessage.class))).thenReturn(mutableRequest);

		when(ogcMessageDistributor.distributeToResponseInterceptor(
				any(MutableHttpServletRequest.class), any(Response.class), any(OgcMessage.class))).thenReturn(resp);

		when(ruleService.findAllRulesForServiceAndEvent(
				any(String.class), any(String.class))).thenReturn(
						getTestInterceptorRulesForServiceAndEvent("WFS", "REQUEST"));

		Response got = gsInterceptorService.interceptGeoServerRequest(httpRequest);

		assertEquals(resp, got);

	}



	@Test
	public void get_most_specific_rule() throws Exception {

		// rule is available
		InterceptorRule expectedRule = new InterceptorRule(
				HttpEnum.EventType.REQUEST,
				InterceptorEnum.RuleType.DENY,
				OgcEnum.ServiceType.WMS,
				OgcEnum.OperationType.GET_MAP,
				"bvb:shinji"
		);

		InterceptorRule actualRule = getMostSpecificRule(
				"WMS", "GetMap", "bvb:shinji", "REQUEST");

		assertTrue(EqualsBuilder.reflectionEquals(expectedRule,
				actualRule, new String[] {"id","created","modified"}));
	}

	@Test
	public void get_operation_specific_rule() throws Exception {

		// only operation is matching
		InterceptorRule expectedRule = new InterceptorRule(
				HttpEnum.EventType.REQUEST,
				InterceptorEnum.RuleType.DENY,
				OgcEnum.ServiceType.WMS,
				OgcEnum.OperationType.GET_CAPABILITIES,
				null
		);

		InterceptorRule actualRule = getMostSpecificRule(
				"WMS", "GetCapabilities", "bvb:shinji", "REQUEST");

		assertTrue(EqualsBuilder.reflectionEquals(expectedRule,
				actualRule, new String[] {"id","created","modified"}));
	}

	@Test
	public void get_service_specific_rule() throws Exception {

		// only service is matching
		InterceptorRule expectedRule = new InterceptorRule(
				HttpEnum.EventType.REQUEST,
				InterceptorEnum.RuleType.DENY,
				OgcEnum.ServiceType.WMS,
				null,
				null
		);

		InterceptorRule actualRule = getMostSpecificRule(
				"WMS", "GetFeatureInfo", "bvb:reus", "REQUEST");

		assertTrue(EqualsBuilder.reflectionEquals(expectedRule,
				actualRule, new String[] {"id","created","modified"}));
	}

	@Test(expected=InterceptorException.class)
	public void get_no_specific_rule() throws Exception {

		getMostSpecificRule("WMS-T", "GetFeatureInfo", "bvb:reus", "REQUEST");

	}

	/**
	 * Basic test utility class for testing single interceptor rules.
	 * @return
	 * @throws Exception
	 */
	private InterceptorRule getMostSpecificRule(String service, String operation,
			String endPoint, String event) throws Exception {

		when(ruleService.findAllRulesForServiceAndEvent(
				any(String.class), any(String.class))).thenReturn(
				getTestInterceptorRulesForServiceAndEvent(service, event));

		// use powermock whitebox reflection to test private class
		InterceptorRule mostSpecificRule = Whitebox.<InterceptorRule> invokeMethod(
				gsInterceptorService,
				"getMostSpecificRule", service, operation, endPoint, event);

		return mostSpecificRule;
	}

	/**
	 * A helper method returning some interceptor rules (out of a predefined
	 * String array).
	 *
	 * Note: In order to mock the behaviour of findAllRulesForServiceAndEvent()
	 *       this must return the rules ordered by endPoint and operation.
	 *
	 * @return
	 */
	private static List<InterceptorRule> getTestInterceptorRulesForServiceAndEvent(
			String service, String event) {

		List<InterceptorRule> mockedRules = new ArrayList<InterceptorRule>();

		if (OgcEnum.ServiceType.fromString(service) == null ||
				HttpEnum.EventType.fromString(event) == null) {
			return mockedRules;
		}

		String[] testRules = new String[] {
				service + ",GetMap,bvb:hummels," + event + ",ALLOW",
				service + ",GetMap,bvb:shinji," + event + ",DENY",
				service + ",GetCapabilities,," + event + ",DENY",
				service + ",GetMap,," + event + ",ALLOW",
				service + ",,," + event + ",DENY",
		};

		for (String testRule : testRules) {
			String[] ruleSpecs = testRule.split(",");
			InterceptorRule rule = new InterceptorRule();
			rule.setService(StringUtils.isNotEmpty(ruleSpecs[0]) ?
					OgcEnum.ServiceType.fromString(ruleSpecs[0]) : null);
			rule.setOperation(StringUtils.isNotEmpty(ruleSpecs[1]) ?
					OgcEnum.OperationType.fromString(ruleSpecs[1]) : null);
			rule.setEndPoint(StringUtils.isNotEmpty(ruleSpecs[2]) ?
					ruleSpecs[2] : null);
			rule.setEvent(StringUtils.isNotEmpty(ruleSpecs[3]) ?
					HttpEnum.EventType.fromString(ruleSpecs[3]) : null);
			rule.setRule(StringUtils.isNotEmpty(ruleSpecs[4]) ?
					InterceptorEnum.RuleType.fromString(ruleSpecs[4]) : null);
			mockedRules.add(rule);
		}

		return mockedRules;
	}

}
