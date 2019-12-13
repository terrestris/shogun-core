package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.InterceptorRuleDao;
import de.terrestris.shoguncore.model.interceptor.InterceptorRule;
import de.terrestris.shoguncore.util.enumeration.HttpEnum;
import de.terrestris.shoguncore.util.enumeration.InterceptorEnum;
import de.terrestris.shoguncore.util.enumeration.OgcEnum;
import de.terrestris.shoguncore.util.http.HttpUtil;
import de.terrestris.shoguncore.util.interceptor.InterceptorException;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.OgcMessage;
import de.terrestris.shoguncore.util.interceptor.OgcMessageDistributor;
import de.terrestris.shoguncore.util.model.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpUtil.class)
@PowerMockIgnore({"javax.management.*", "org.mockito.*", "org.powermock.*", "org.apache.commons.*", "org.junit.*", "javax.xml.*", "org.xml.*", "org.apache.logging.log4j.*"})
public class GeoServerInterceptorServiceTest {

    private final String TEST_GEOSERVER_BASE_PATH = "http://localhost:1234/geoserver/";

    @InjectMocks
    GeoServerInterceptorService gsInterceptorService;

    @Mock(name = "interceptorRuleService")
    InterceptorRuleService<InterceptorRule, InterceptorRuleDao<InterceptorRule>> ruleService;

    @Mock
    OgcMessageDistributor ogcMessageDistributor;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(gsInterceptorService, "namespaceBoundUrl", true);

        Properties geoServerNameSpaces = new Properties();
        geoServerNameSpaces.setProperty("bvb", TEST_GEOSERVER_BASE_PATH + "bvb/ows");
        gsInterceptorService.setGeoServerNameSpaces(geoServerNameSpaces);
    }

    @Test(expected = InterceptorException.class)
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
        when(HttpUtil.get(any(URI.class), any(Header[].class))).thenReturn(resp);

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
    public void send_wms_get_to_reflector() throws InterceptorException,
        URISyntaxException, HttpException, IOException {

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setRequestURI("http://example.com/geoserver.action");
        httpRequest.setParameter("LAYERS", "bvb:yarmolenko");
        httpRequest.setParameter("useReflect", "true");
        httpRequest.setMethod("GET");

        PowerMockito.mockStatic(HttpUtil.class);
        Response resp = new Response();
        when(HttpUtil.get(any(URI.class), any(Header[].class))).thenReturn(resp);

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
        when(HttpUtil.post(any(URI.class), any(List.class), any(Header[].class))).thenReturn(resp);

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
        URI expectedPostUrl = new URI(url + "?" + queryString);

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
            HttpUtil.post(eq(expectedPostUrl), any(List.class), any(Header[].class))
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
        when(HttpUtil.post(any(URI.class), any(List.class), any(Header[].class))).thenReturn(resp);

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
        URI expectedPostUrl = new URI(url + "?" + queryString);

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
            HttpUtil.post(eq(expectedPostUrl), any(List.class), any(Header[].class))
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

        InterceptorRule expectedRule = new InterceptorRule(
            HttpEnum.EventType.REQUEST,
            InterceptorEnum.RuleType.MODIFY,
            OgcEnum.ServiceType.WMS,
            OgcEnum.OperationType.GET_STYLES,
            "bvb:yarmolenko"
        );

        InterceptorRule actualRule = getMostSpecificRule(
            "WMS", "GetStyles", "bvb:yarmolenko", "REQUEST");

        assertTrue(EqualsBuilder.reflectionEquals(expectedRule,
            actualRule, "id", "created", "modified"));
    }

    @Test
    public void get_operation_specific_rule() throws Exception {

        // only operation is matching
        InterceptorRule expectedRule = new InterceptorRule(
            HttpEnum.EventType.REQUEST,
            InterceptorEnum.RuleType.ALLOW,
            OgcEnum.ServiceType.WFS,
            OgcEnum.OperationType.DESCRIBE_FEATURE_TYPE,
            null
        );

        InterceptorRule actualRule = getMostSpecificRule(
            "WFS", "DescribeFeatureType", null, "REQUEST");

        assertTrue(EqualsBuilder.reflectionEquals(expectedRule,
            actualRule, "id", "created", "modified"));
    }

    @Test
    public void get_operation_and_service_specific_rule() throws Exception {
        // only endpoint and service are matching
        InterceptorRule expectedRule = new InterceptorRule(
            HttpEnum.EventType.REQUEST,
            InterceptorEnum.RuleType.ALLOW,
            OgcEnum.ServiceType.WFS,
            OgcEnum.OperationType.DESCRIBE_FEATURE_TYPE,
            null
        );

        InterceptorRule actualRule = getMostSpecificRule(
            "WFS", "DescribeFeatureType", null, "REQUEST");

        assertTrue(EqualsBuilder.reflectionEquals(expectedRule,
            actualRule, "id", "created", "modified"));
    }

    @Test
    public void get_service_and_endpoint_specific_rule() throws Exception {

        // only service is matching
        InterceptorRule expectedRule = new InterceptorRule(
            HttpEnum.EventType.REQUEST,
            InterceptorEnum.RuleType.MODIFY,
            OgcEnum.ServiceType.WMS,
            OgcEnum.OperationType.GET_STYLES,
            "bvb:yarmolenko"
        );

        InterceptorRule actualRule = getMostSpecificRule(
            "WMS", null, "bvb:yarmolenko", "REQUEST");

        assertTrue(EqualsBuilder.reflectionEquals(expectedRule,
            actualRule, "id", "created", "modified"));
    }

    @Test(expected = InterceptorException.class)
    public void get_no_specific_rule() throws Exception {
        getMostSpecificRule("WMS-T", "GetFeatureInfo", "bvb:reus", "REQUEST");
    }

    @Test
    public void test_appendQueryString() throws URISyntaxException {
        URI uri = new URI("http://example.com/index.html");
        String appendQuery = "foo=bar&humpty=dumpty";
        URI got = GeoServerInterceptorService.appendQueryString(uri, appendQuery);
        assertEquals("http://example.com/index.html?foo=bar&humpty=dumpty", got.toString());
    }

    @Test
    public void test_appendQueryString_appendsToExistingParams() throws URISyntaxException {
        URI uri = new URI("http://example.com/index.html?FC=effzeh");
        String appendQuery = "foo=bar&humpty=dumpty";
        URI got = GeoServerInterceptorService.appendQueryString(uri, appendQuery);
        assertEquals("http://example.com/index.html?FC=effzeh&foo=bar&humpty=dumpty", got.toString());
    }

    @Test
    public void test_appendQueryString_appendsToExistingParamsOfEqualName() throws URISyntaxException {
        URI uri = new URI("http://example.com/index.html?foo=baz");
        String appendQuery = "foo=bar&humpty=dumpty";
        URI got = GeoServerInterceptorService.appendQueryString(uri, appendQuery);
        assertEquals("http://example.com/index.html?foo=baz&foo=bar&humpty=dumpty", got.toString());
    }

    @Test
    public void test_appendQueryString_handlesHttpsScheme() throws URISyntaxException {
        URI uri = new URI("https://example.com/index.html");
        String appendQuery = "foo=bar&humpty=dumpty";
        URI got = GeoServerInterceptorService.appendQueryString(uri, appendQuery);
        assertEquals("https://example.com/index.html?foo=bar&humpty=dumpty", got.toString());
    }

    @Test
    public void test_appendQueryString_handlesAuthority() throws URISyntaxException {
        URI uri = new URI("http://user:secret@example.com/index.html");
        String appendQuery = "foo=bar&humpty=dumpty";
        URI got = GeoServerInterceptorService.appendQueryString(uri, appendQuery);
        assertEquals("http://user:secret@example.com/index.html?foo=bar&humpty=dumpty", got.toString());
    }

    @Test
    public void test_appendQueryString_handlesFragment() throws URISyntaxException {
        URI uri = new URI("http://example.com/index.html#my-fragment");
        String appendQuery = "foo=bar&humpty=dumpty";
        URI got = GeoServerInterceptorService.appendQueryString(uri, appendQuery);
        assertEquals("http://example.com/index.html?foo=bar&humpty=dumpty#my-fragment", got.toString());
    }

    @Test
    public void test_appendQueryString_handlesComplexCombination() throws URISyntaxException {
        URI uri = new URI("https://user:secret@example.com/index.html#my-fragment");
        String appendQuery = "foo=bar&humpty=dumpty";
        URI got = GeoServerInterceptorService.appendQueryString(uri, appendQuery);
        assertEquals("https://user:secret@example.com/index.html?foo=bar&humpty=dumpty#my-fragment", got.toString());
    }

    @Test
    public void test_appendQueryString_no_uri_no_appendQuery() {
        URI uri = null;
        String appendQuery = null;
        URI got = GeoServerInterceptorService.appendQueryString(uri, appendQuery);
        // we don't throw, but should always return null
        assertNull(got);
    }

    @Test
    public void test_appendQueryString_no_uri() {
        URI uri = null;
        String appendQuery = "foo=bar&humpty=dumpty";
        URI got = GeoServerInterceptorService.appendQueryString(uri, appendQuery);
        // we don't throw, but should always return null
        assertNull(got);
    }

    @Test
    public void test_appendQueryString_no_appendQuery() throws URISyntaxException {
        URI uri = new URI("http://example.com/index.html");
        String appendQuery = null;
        URI got = GeoServerInterceptorService.appendQueryString(uri, appendQuery);
        // return the passed uri
        assertEquals(uri, got);
    }

    @Test
    public void test_getGeoServerBaseURI_reflector() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, URISyntaxException {
        final String methodName = "getGeoServerBaseURI";
        final Method methodToCheck = GeoServerInterceptorService.class.getDeclaredMethod(methodName, OgcMessage.class, boolean.class);
        methodToCheck.setAccessible(true); // set method temporarily public for testing

        final OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WMS, null, "bvb:yarmolenko", null, null);
        final boolean useReflect = true;

        Object resultObj = methodToCheck.invoke(this.gsInterceptorService, message, useReflect);
        assertTrue(resultObj instanceof URI);

        URI resultUri = (URI) resultObj;
        assertEquals("Generated GeoServer URI path does not contain reflector endpoint", (new URI(TEST_GEOSERVER_BASE_PATH)).getPath() + "bvb/wms/reflect", resultUri.getPath());
    }

    @Test
    public void test_getGeoServerBaseURI_use_reflect_wfs_do_not_modify_endpoint() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, URISyntaxException {
        final String methodName = "getGeoServerBaseURI";
        final Method methodToCheck = GeoServerInterceptorService.class.getDeclaredMethod(methodName, OgcMessage.class, boolean.class);
        methodToCheck.setAccessible(true); // set method temporarily public for testing

        OgcEnum.ServiceType[] typesToCheck = {
            OgcEnum.ServiceType.WFS,
            OgcEnum.ServiceType.WCS,
            OgcEnum.ServiceType.WPS
        };

        for (OgcEnum.ServiceType typeToCheck : typesToCheck) {
            final OgcMessage message = new OgcMessage(typeToCheck, null, "bvb:yarmolenko", null, null);
            final boolean useReflect = true;

            Object resultObj = methodToCheck.invoke(this.gsInterceptorService, message, useReflect);
            assertTrue(resultObj instanceof URI);

            URI resultUri = (URI) resultObj;
            assertEquals("GeoServer URL to call should not be modified for service type: " + typeToCheck.name(), (new URI(TEST_GEOSERVER_BASE_PATH)).getPath() + "bvb/ows", resultUri.getPath());
        }
    }

    /**
     * Basic test utility class for testing single interceptor rules.
     *
     * @return
     * @throws Exception
     */
    private InterceptorRule getMostSpecificRule(String service, String operation,
                                                String endPoint, String event) throws Exception {

        when(ruleService.findAllRulesForServiceAndEvent(
            any(String.class), any(String.class))).thenReturn(
            getTestInterceptorRulesForServiceAndEvent(service, event));

        // use powermock whitebox reflection to test private class
        InterceptorRule mostSpecificRule = Whitebox.invokeMethod(
            gsInterceptorService,
            "getMostSpecificRule", service, operation, endPoint, event);

        return mostSpecificRule;
    }

    /**
     * A helper method returning some interceptor rules (out of a predefined
     * String array).
     * <p>
     * Note: In order to mock the behaviour of findAllRulesForServiceAndEvent()
     * this must return the rules ordered by endPoint and operation.
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

        String[] testRules = new String[]{
            service + ",,," + event + ",DENY",
            service + ",GetMap,bvb:hummels," + event + ",ALLOW",
            service + ",GetFeatureInfo,bvb:shinji," + event + ",MODIFY",
            service + ",GetMap,bvb:shinji," + event + ",DENY",
            service + ",GetCapabilities,bvb:shinji," + event + ",DENY",
            service + ",DescribeFeatureType,," + event + ",ALLOW",
            service + ",GetMap,," + event + ",ALLOW",
            service + ",GetFeatureInfo,," + event + ",DENY",
            service + ",GetStyles,bvb:yarmolenko," + event + ",MODIFY",
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
