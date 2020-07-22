package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.LayerDao;
import de.terrestris.shoguncore.dao.UserDao;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.UserGroup;
import de.terrestris.shoguncore.model.interceptor.InterceptorRule;
import de.terrestris.shoguncore.model.layer.Layer;
import de.terrestris.shoguncore.model.layer.source.WmtsLayerDataSource;
import de.terrestris.shoguncore.model.security.PermissionCollection;
import de.terrestris.shoguncore.util.enumeration.HttpEnum;
import de.terrestris.shoguncore.util.enumeration.OgcEnum;
import de.terrestris.shoguncore.util.enumeration.OgcEnum.OperationType;
import de.terrestris.shoguncore.util.enumeration.OgcEnum.ServiceType;
import de.terrestris.shoguncore.util.http.HttpUtil;
import de.terrestris.shoguncore.util.interceptor.*;
import de.terrestris.shoguncore.util.model.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author Daniel Koch
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 */
@Service
public class GeoServerInterceptorService {

    /**
     * The Logger.
     */
    private static final Logger LOG = getLogger(
        GeoServerInterceptorService.class);

    /**
     * An array of whitelisted Headers to forward within the Interceptor.
     */
    private static final String[] FORWARD_RESPONSE_HEADER_KEYS = new String[]{
        "Content-Type",
        "Content-Disposition",
        "Content-Language",
        "geowebcache-cache-result",
        "geowebcache-crs",
        "geowebcache-gridset",
        "geowebcache-tile-bounds",
        "geowebcache-tile-index",
        "geowebcache-miss-reason"
    };

    /**
     *
     */
    private static final Pattern WMTS_PATTERN = Pattern.compile("/[^/]+/wmts.action/\\d+/(.*)");
    private static final String WMS_REFLECT_ENDPOINT = "/reflect";
    private static final String USE_REFLECT_PARAM = "useReflect";

    /**
     *
     */
    @Autowired
    OgcMessageDistributor ogcMessageDistributor;

    /**
     *
     */
    @Autowired
    InterceptorRuleService<InterceptorRule, ?> interceptorRuleService;

    /**
     *
     */
    @Autowired
    @Qualifier("layerService")
    protected LayerService<Layer, LayerDao<Layer>> layerService;

    /**
     *
     */
    @Autowired
    @Qualifier("layerDao")
    protected LayerDao<Layer> layerDao;

    /**
     *
     */
    @Autowired
    @Qualifier("userService")
    protected UserService<User, UserDao<User>> userService;

    /**
     * The autowired properties file containing the (application driven)
     * GeoServer namespace - GeoServer BaseURI mapping, e.g.:
     * <p>
     * topp=http://localhost:8080/geoserver/topp/ows
     */
    private Properties geoServerNameSpaces;

    @Value("${geoserver.interceptor.namespaceBoundUrl:true}")
    private boolean namespaceBoundUrl;

    @Value("${geoserver.interceptor.defaultOwsUrl:}")
    private String defaultOwsUrl;

    /**
     * @param params
     * @return
     */
    private static List<NameValuePair> createQueryParams(Map<String, String[]> params) {

        List<NameValuePair> queryParams = new ArrayList<NameValuePair>();

        for (Entry<String, String[]> param : params.entrySet()) {
            queryParams.add(new BasicNameValuePair(param.getKey(),
                StringUtils.join(param.getValue(), ",")));
        }

        return queryParams;
    }

    /**
     * @param baseUri
     * @param queryParams
     * @return
     * @throws URISyntaxException
     */
    private static URI getFullRequestURI(URI baseUri, List<NameValuePair> queryParams)
        throws URISyntaxException {

        URI requestUri = null;

        URIBuilder builder = new URIBuilder(baseUri);
        builder.addParameters(queryParams);
        requestUri = builder.build();

        return requestUri;
    }

    /**
     * @param endPoint
     */
    private static String getGeoServerNameSpace(String endPoint) {

        // return the endPoint as nameSpace per default
        String geoServerNamespace = endPoint;

        if (endPoint.contains(":")) {
            String[] split = endPoint.split(":");
            geoServerNamespace = split[0];
        }

        return geoServerNamespace;
    }

    /**
     * @param request
     * @throws InterceptorException
     * @throws HttpException
     */
    public static Response sendRequest(MutableHttpServletRequest request)
        throws InterceptorException, HttpException {

        Response httpResponse = new Response();

        String requestMethod = request.getMethod();
        boolean getRequest = "GET".equalsIgnoreCase(requestMethod);
        boolean postRequest = "POST".equalsIgnoreCase(requestMethod);

        try {

            // get the request URI
            URI requestUri = new URI(request.getRequestURI());

            Header[] requestHeaders = getRequestHeadersToForward(request);

            // get the query parameters provided by the GET/POST request and
            // convert to a list of NameValuePairs
            List<NameValuePair> allQueryParams = createQueryParams(request.getParameterMap());

            // append the given request parameters to the base URI
            URI fullRequestUri = getFullRequestURI(requestUri, allQueryParams);

            if (getRequest) {
                // if we're called via GET method

                // perform the request with the given parameters
                httpResponse = HttpUtil.get(fullRequestUri, requestHeaders);

            } else if (postRequest) {
                // if we're called via POST method

                // We have to attach the actual query; a POST to e.g. http://example.com/?foo=bar is totally OK
                String queryString = request.getQueryString();
                if (queryString != null) {
                    requestUri = appendQueryString(requestUri, queryString);
                }

                // get the request body if any
                String body = OgcXmlUtil.getRequestBody(request);

                if (!StringUtils.isEmpty(body)) {
                    // we do have a POST with string data present

                    // parse the content type of the request
                    ContentType contentType = ContentType.parse(request.getContentType());

                    if (contentType.getCharset() == null) {
                        // use UTF-8 charset if charset could not be parsed from the content type
                        // of the request
                        contentType = contentType.withCharset("UTF-8");
                    }

                    // perform the POST request to the URI with queryString and with the given body
                    httpResponse = HttpUtil.post(requestUri, body, contentType, requestHeaders);
                } else {

                    // perform the POST request with the given name value pairs,
                    httpResponse = HttpUtil.post(requestUri, allQueryParams, requestHeaders);
                }

            } else {
                // otherwise throw an exception
                throw new InterceptorException("Only GET or POST method is allowed");
            }

        } catch (URISyntaxException | UnsupportedEncodingException e) {
            LOG.error("Error while sending request: " + e.getMessage());
        }

        return httpResponse;
    }

    /**
     * @param request
     * @return
     */
    private static Header[] getRequestHeadersToForward(MutableHttpServletRequest request) {
        List<Header> requestHeaderList = new ArrayList<>();

        // forward x-geoserver-credentials as Authorization header if available
        String credentials = request.getHeader("x-geoserver-credentials");
        if (credentials != null) {
            requestHeaderList.add(new BasicHeader(HttpHeaders.AUTHORIZATION, credentials));
        }

        return requestHeaderList.toArray(new Header[0]);
    }

    /**
     * Returns a new URI with the passed queryString (e.g. foo=bar&amp;baz=123) appended to the passed URI. Adjusted from
     * http://stackoverflow.com/a/26177982.
     *
     * @param uri
     * @param appendQuery
     */
    public static URI appendQueryString(URI uri, String appendQuery) {
        if (uri == null || appendQuery == null || appendQuery.isEmpty()) {
            return uri;
        }
        String newQuery = uri.getQuery();
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery += "&" + appendQuery;
        }
        // Fallback is the old URI
        URI newUri = uri;
        try {
            newUri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), newQuery, uri.getFragment());
        } catch (URISyntaxException e) {
            String msg = String.format(
                "Failed to append query '%s' to URI '%s', returning URI unchanged.",
                appendQuery, uri
            );
            LOG.warn(msg);
        }
        return newUri;
    }

    /**
     * @return
     * @throws UnsupportedEncodingException
     */
    private static HttpHeaders getResponseHeadersToForward(HttpHeaders headers)
        throws UnsupportedEncodingException {

        HttpHeaders responseHeaders = new HttpHeaders();

        if (headers == null) {
            LOG.debug("No headers found to forward!");
            return responseHeaders;
        }

        LOG.trace("Requested to filter the Headers to respond with:");

        for (Entry<String, List<String>> header : headers.entrySet()) {
            String headerKey = header.getKey();
            String headerVal = StringUtils.join(header.getValue(), ",");

            LOG.trace("  * Header: " + headerKey);

            if (Arrays.asList(FORWARD_RESPONSE_HEADER_KEYS).contains(headerKey)) {

                // the GeoServer response may contain a subtype in the
                // "Content-Type" header without double quotes surrounding the
                // subtype's value. If this is set we need to surround it
                // with double quotes as this is required by the Spring
                // ResponseEntity (and the RFC 2616 standard).
                Pattern pattern = Pattern.compile("subtype=(.*)");
                Matcher matcher = pattern.matcher(headerVal);

                if (matcher.find()) {
                    String replaceCandidate = matcher.group(1);
                    String replacer;

                    replacer = StringUtils.prependIfMissing(
                        replaceCandidate, "\"");
                    replacer = StringUtils.appendIfMissing(
                        replacer, "\"");

                    headerVal = StringUtils.replace(headerVal,
                        replaceCandidate, replacer);
                }

                responseHeaders.set(headerKey, headerVal);
                LOG.trace("    > Forwarded");
            } else {
                LOG.trace("    > Skipped");
            }
        }

        return responseHeaders;
    }

    @Transactional
    public Response interceptWmtsRequest(HttpServletRequest request, String serviceId) throws UnsupportedEncodingException, InterceptorException, HttpException, URISyntaxException {
        Matcher matcher = WMTS_PATTERN.matcher(request.getRequestURI());
        int id = Integer.parseInt(serviceId);
        if (!matcher.matches()) {
            throw new InterceptorException("No WMTS request path found!");
        }
        String path = matcher.group(1);

        // get all layers allowed for this user in order to filter out not allowed ones
        User user = userService.getUserBySession();
        if (user == null) {
            // check for anonymous access
            List<Layer> layers = layerDao.findAll();
            WmtsLayerDataSource dataSource = null;
            for (Layer layer : layers) {
                if (layer.getSource() instanceof WmtsLayerDataSource) {
                    WmtsLayerDataSource source = (WmtsLayerDataSource) layer.getSource();
                    if (source.getId().equals(id)) {
                        Map<UserGroup, PermissionCollection> userGroupPermissions = layer.getGroupPermissions();
                        if (userGroupPermissions != null) {
                            for (UserGroup group : userGroupPermissions.keySet()) {
                                if (group.getName().equalsIgnoreCase("public") &&
                                    group.getOwner() == null) {
                                    dataSource = source;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            List<Layer> layers = layerService.findAll();
            WmtsLayerDataSource dataSource = null;
            for (Layer layer : layers) {
                if (layer.getSource() instanceof WmtsLayerDataSource) {
                    WmtsLayerDataSource source = (WmtsLayerDataSource) layer.getSource();
                    if (source.getId().equals(id)) {
                        dataSource = source;
                        break;
                    }
                }
            }
        }

        if (dataSource == null) {
            return new Response(HttpStatus.FORBIDDEN, null, new byte[0]);
        }

        String baseUrl = dataSource.getUrl();
        Response response = HttpUtil.get(baseUrl + "/" + path);

        HttpHeaders forwardingHeaders = getResponseHeadersToForward(response.getHeaders());
        response.setHeaders(forwardingHeaders);

        return response;
    }

    /**
     * Calls the main method with empty optionals hashmap
     * @param request
     * @return
     * @throws InterceptorException
     * @throws URISyntaxException
     * @throws HttpException
     * @throws IOException
     */
    public Response interceptGeoServerRequest(HttpServletRequest request)
            throws InterceptorException, URISyntaxException, HttpException, IOException {
        return interceptGeoServerRequest(request, new HashMap<String, Optional<String>>());
    }

    /**
     *
     * @param request
     * @param optionals
     * @return
     * @throws InterceptorException
     * @throws URISyntaxException
     * @throws HttpException
     * @throws IOException
     */
    public Response interceptGeoServerRequest(
        HttpServletRequest request,
        HashMap<String, Optional<String>> optionals)
        throws InterceptorException, URISyntaxException,
        HttpException, IOException {

        // wrap the request, we want to manipulate it
        MutableHttpServletRequest mutableRequest =
            new MutableHttpServletRequest(request);
        if (optionals.containsKey("endpoint") && optionals.get("endpoint").isPresent()) {
            mutableRequest.addParameter("CUSTOM_ENDPOINT", optionals.get("endpoint").get());
            mutableRequest.addParameter("CONTEXT_PATH", request.getContextPath());
        }

        // check if we have a RESTful WMTS request
        boolean isRestfulWmts = false;
        boolean isRestfulWmtsGetFeatureinfo = false;
        if (optionals.containsKey("layername") && optionals.get("layername").isPresent() &&
            optionals.containsKey("style") && optionals.get("style").isPresent() &&
            optionals.containsKey("tilematrixset") && optionals.get("tilematrixset").isPresent() &&
            optionals.containsKey("tilematrix") && optionals.get("tilematrix").isPresent() &&
            optionals.containsKey("tilerow") && optionals.get("tilerow").isPresent() &&
            optionals.containsKey("tilecol") && optionals.get("tilecol").isPresent()) {
                isRestfulWmts = true;
                if (optionals.containsKey("j") && optionals.get("j").isPresent() &&
                    optionals.containsKey("i") && optionals.get("i").isPresent()) {
                    isRestfulWmtsGetFeatureinfo = true;
                }
        }
        // get the OGC message information (service, request, endPoint)
        OgcMessage message = getOgcMessage(mutableRequest, isRestfulWmts, isRestfulWmtsGetFeatureinfo);

        // check whether WMS reflector endpoint should be called
        final boolean useWmsReflector = shouldReflectEndpointBeCalled(mutableRequest, message);

        // get the GeoServer base URI by the provided request
        URI geoServerBaseUri = getGeoServerBaseURI(message, useWmsReflector);

        // set the GeoServer base URI to the (wrapped) request
        mutableRequest.setRequestURI(geoServerBaseUri);

        // intercept the request (if needed)
        mutableRequest = ogcMessageDistributor
            .distributeToRequestInterceptor(mutableRequest, message, optionals);

        // send the request
        // TODO: Move to global proxy class
        Response response = sendRequest(mutableRequest);

        // intercept the response (if needed)
        Response interceptedResponse = ogcMessageDistributor
            .distributeToResponseInterceptor(mutableRequest, response, message);

        // finally filter the white-listed response headers
        // TODO: Move to global proxy class
        HttpHeaders forwardingHeaders = getResponseHeadersToForward(
            interceptedResponse.getHeaders()
        );
        interceptedResponse.setHeaders(forwardingHeaders);

        return interceptedResponse;
    }

    /**
     * Detect whether the WMS reflector endpoint of GeoServer should be called instead of the one defined in provided message
     *
     * @param mutableRequest request to check
     * @param message        instance of {@link OgcMessage}
     * @return true if useReflect found in parameters, false otherwise
     * @throws InterceptorException
     * @throws IOException
     */
    private boolean shouldReflectEndpointBeCalled(MutableHttpServletRequest mutableRequest, OgcMessage message) throws InterceptorException, IOException {
        boolean useReflect = false;

        if (message.getService() != ServiceType.WMS) {
            return useReflect;
        }

        String value = MutableHttpServletRequest.getRequestParameterValue(mutableRequest, USE_REFLECT_PARAM);
        useReflect = Boolean.valueOf(value);
        if (useReflect) {
            LOG.info("Parameter " + USE_REFLECT_PARAM + "found in request. Will use WMS reflector endpoint of GeoServer.");
        }

        return useReflect;
    }

    /**
     * @param mutableRequest
     * @param isRestfulWmtsGetFeatureinfo
     * @param isRestfulWmts
     * @return
     * @throws InterceptorException
     * @throws IOException
     */
    private OgcMessage getOgcMessage(MutableHttpServletRequest mutableRequest, boolean isRestfulWmts,
            boolean isRestfulWmtsGetFeatureinfo)
        throws InterceptorException, IOException {

        LOG.trace("Building the OGC message from the given request.");

        OgcMessage ogcMessage = new OgcMessage();

        String requestService = MutableHttpServletRequest.getRequestParameterValue(
            mutableRequest, OgcEnum.Service.SERVICE.toString());
        String requestOperation = MutableHttpServletRequest.getRequestParameterValue(
            mutableRequest, OgcEnum.Operation.OPERATION.toString());
        String requestEndPoint = MutableHttpServletRequest.getRequestParameterValue(
            mutableRequest, OgcEnum.EndPoint.getAllValues());

        if (isRestfulWmts) {
            requestService = ServiceType.WMTS.toString();
            String format = mutableRequest.getParameterIgnoreCase("format");
            if (isRestfulWmtsGetFeatureinfo) {
                requestOperation = OperationType.GET_FEATURE_INFO.toString();
            } else if (format != null && format.toLowerCase(Locale.ROOT).contains("image")) {
                requestOperation = OperationType.GET_TILE.toString();
            } else {
                requestOperation = OperationType.GET_CAPABILITIES.toString();
            }
        }

        if (StringUtils.isEmpty(requestService) ||
            StringUtils.isEmpty(requestOperation) ||
            StringUtils.isEmpty(requestEndPoint)) {
            if (!StringUtils.isEmpty(requestEndPoint) &&
                !StringUtils.isEmpty(MutableHttpServletRequest.getRequestParameterValue(
                    mutableRequest, USE_REFLECT_PARAM))
            ) {
                LOG.trace("Will use WMS reflector endpoint of GeoServer");
                requestService = ServiceType.WMS.toString();
                requestOperation = OperationType.GET_MAP.toString();
            } else {
                throw new InterceptorException("Couldn't find all required OGC " +
                    "parameters (SERVICE, REQUEST, ENDPOINT). Please check the " +
                    "validity of the request.");
            }
        }

        if (StringUtils.isNotEmpty(requestService)) {
            ogcMessage.setService(OgcEnum.ServiceType.fromString(requestService));
            LOG.trace("Successfully set the service: " +
                OgcEnum.ServiceType.fromString(requestService));
        } else {
            LOG.debug("No service found.");
        }

        if (StringUtils.isNotEmpty(requestOperation)) {
            ogcMessage.setOperation(OgcEnum.OperationType.fromString(requestOperation));
            LOG.trace("Successfully set the operation: " +
                OgcEnum.OperationType.fromString(requestOperation));
        } else {
            LOG.debug("No operation found.");
        }

        if (StringUtils.isNotEmpty(requestEndPoint)) {
            ogcMessage.setEndPoint(requestEndPoint);
            LOG.trace("Successfully set the endPoint: " + requestEndPoint);
        } else {
            LOG.debug("No endPoint found.");
        }

        InterceptorRule mostSpecificRequestRule = getMostSpecificRule(requestService,
            requestOperation, requestEndPoint, HttpEnum.EventType.REQUEST.toString());
        InterceptorRule mostSpecificResponseRule = getMostSpecificRule(requestService,
            requestOperation, requestEndPoint, HttpEnum.EventType.RESPONSE.toString());

        if (mostSpecificRequestRule != null) {
            ogcMessage.setRequestRule(mostSpecificRequestRule.getRule());
            LOG.trace("Successfully set the requestRule: " +
                mostSpecificRequestRule.getRule());
        } else {
            LOG.debug("No interceptor rule found for the request.");
        }

        if (mostSpecificResponseRule != null) {
            ogcMessage.setResponseRule(mostSpecificResponseRule.getRule());
            LOG.trace("Successfully set the responseRule: " +
                mostSpecificResponseRule.getRule());
        } else {
            LOG.debug("No interceptor rule found for the response.");
        }

        LOG.trace("Successfully build the OGC message: " + ogcMessage);

        return ogcMessage;
    }

    /**
     * @param requestService
     * @param requestOperation
     * @param requestEndPoint
     * @param ruleEvent
     * @return
     * @throws InterceptorException
     */
    private InterceptorRule getMostSpecificRule(String requestService,
                                                String requestOperation, String requestEndPoint,
                                                String ruleEvent) throws InterceptorException {

        final ServiceType service = OgcEnum.ServiceType.fromString(
            requestService);
        final OperationType operation = OgcEnum.OperationType.fromString(
            requestOperation);
        final String endPoint = requestEndPoint;

        LOG.trace("Finding the most specific interceptor rule for: \n" +
            "  * Event: " + ruleEvent + "\n" +
            "  * Service: " + service + "\n" +
            "  * Operation: " + operation + "\n" +
            "  * EndPoint: " + endPoint
        );

        // get all persisted rules for the given service and event
        List<InterceptorRule> interceptorRules = this.interceptorRuleService
            .findAllRulesForServiceAndEvent(requestService, ruleEvent);

        LOG.trace("Got " + interceptorRules.size() + " rule(s) from database.");

        if (LOG.isTraceEnabled()) {
            for (InterceptorRule interceptorRule : interceptorRules) {
                LOG.trace("Returned rule is: " + interceptorRule);
            }
        }

        LOG.trace("Evaluating the given rules for the most specific one:");

        // create the predicate for finding the most specific rule out of
        // the given rules (conditions in descending specific order).
        // note: we don't need to check for request event (request/response)
        // and the service type at all as the rule candidates are filtered by
        // the DAO method already. the last check for service specificity found
        // here is a fallback only.
        HashMap<InterceptorRule, Integer> ruleMap = new HashMap<>();
        interceptorRules.stream().forEach((rule) -> {
            int score = 0;

            if (!StringUtils.isEmpty(rule.getEndPoint()) && !StringUtils.isEmpty(endPoint) && !StringUtils.equalsIgnoreCase(rule.getEndPoint(), endPoint)) {
                return;
            }
            if (rule.getService() != null && !Objects.equals(rule.getService(), service) && service != null) {
                return;
            }
            if (rule.getOperation() != null && !Objects.equals(rule.getOperation(), operation) && operation != null) {
                return;
            }
            if (endPoint != null && Objects.equals(rule.getEndPoint(), endPoint)) {
                ++score;
            }
            if (operation != null && Objects.equals(rule.getOperation(), operation)) {
                ++score;
            }
            if (service != null && Objects.equals(rule.getService(), service)) {
                ++score;
            }
            ruleMap.put(rule, score);
        });

        AtomicReference<Integer> biggestScore = new AtomicReference<>(0);
        AtomicReference<InterceptorRule> mostSpecific = new AtomicReference<>();

        ruleMap.entrySet().stream().forEach((entry) -> {
            if (entry.getValue() > biggestScore.get()) {
                mostSpecific.set(entry.getKey());
                biggestScore.set(entry.getValue());
            }
        });

        if (interceptorRules.size() == 0) {
            LOG.error("Got no interceptor rules for this request/response. " +
                "Usually this should not happen as one has to define at " +
                "least the basic sets of rules (e.g. ALLOW all WMS " +
                "requests) when using the interceptor.");
            throw new InterceptorException("No interceptor rule found.");
        } else if (LOG.isTraceEnabled()) {
            LOG.trace("Identified the following rule as most the specific " +
                "one: " + mostSpecific.get());
        }

        return mostSpecific.get();
    }

    /**
     * @param geoServerNamespace
     * @param useWmsReflector
     * @param isWMS
     * @throws URISyntaxException
     * @throws InterceptorException
     */
    public URI getGeoServerBaseURIFromNameSpace(String geoServerNamespace, boolean useWmsReflector, boolean isWMS)
        throws URISyntaxException, InterceptorException {

        URI uri = null;

        String geoServerUrl;
        if (namespaceBoundUrl) {
            geoServerUrl = this.geoServerNameSpaces.getProperty(
                geoServerNamespace);
        } else {
            geoServerUrl = defaultOwsUrl;
            LOG.debug("Using GeoServer OWS URL without namespace: {}" , geoServerUrl);
        }

        if (StringUtils.isEmpty(geoServerUrl)) {
            throw new InterceptorException("Couldn't detect GeoServer URI " +
                "from the given namespace");
        }

        if (useWmsReflector && isWMS) {
            LOG.trace("Will use WMS reflector endpoint");
            if (StringUtils.endsWithIgnoreCase(geoServerUrl, "ows")) {
                StringBuilder builder = new StringBuilder();
                int start = geoServerUrl.lastIndexOf("ows");
                builder.append(geoServerUrl, 0, start);
                builder.append("wms" + WMS_REFLECT_ENDPOINT);
                geoServerUrl = builder.toString();
            } else if (StringUtils.endsWithIgnoreCase(geoServerUrl, "wms")) {
                geoServerUrl += WMS_REFLECT_ENDPOINT;
            }
            LOG.trace("The modified endpoint is: " + geoServerUrl);
        }

        URIBuilder builder = new URIBuilder(geoServerUrl);

        uri = builder.build();

        return uri;
    }

    /**
     * @param message
     * @throws URISyntaxException
     * @throws InterceptorException
     */
    private URI getGeoServerBaseURI(OgcMessage message, boolean useWmsReflector) throws URISyntaxException,
        InterceptorException {

        LOG.debug("Finding the GeoServer base URI by the provided EndPoint: " +
            message.getEndPoint());

        // get the namespace from the qualified endPoint name
        String geoServerNamespace = getGeoServerNameSpace(message.getEndPoint());

        LOG.trace("Found the following GeoServer namespace set in the "
            + "EndPoint: " + geoServerNamespace);

        // set the GeoServer base URL
        URI geoServerBaseUri = getGeoServerBaseURIFromNameSpace(geoServerNamespace, useWmsReflector, message.isWms());

        LOG.debug("The corresponding GeoServer base URI is: " + geoServerBaseUri);

        return geoServerBaseUri;
    }

    /**
     * @param ogcMessageDistributor the ogcMessageDistributor to set
     */
    public void setOgcMessageDistributor(OgcMessageDistributor ogcMessageDistributor) {
        this.ogcMessageDistributor = ogcMessageDistributor;
    }


    /**
     * @param interceptorRuleService the interceptorRuleService to set
     */
    public void setInterceptorRuleService(
        InterceptorRuleService<InterceptorRule, ?> interceptorRuleService) {
        this.interceptorRuleService = interceptorRuleService;
    }

    /**
     * @return the geoServerNameSpaces
     */
    public Properties getGeoServerNameSpaces() {
        return geoServerNameSpaces;
    }

    /**
     * @param geoServerNameSpaces the geoServerNameSpaces to set
     */
    @Autowired
    @Qualifier("geoServerNameSpaces")
    public void setGeoServerNameSpaces(Properties geoServerNameSpaces) {
        this.geoServerNameSpaces = geoServerNameSpaces;
    }

}
