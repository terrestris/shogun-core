package de.terrestris.shogun2.service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import de.terrestris.shogun2.model.interceptor.InterceptorRule;
import de.terrestris.shogun2.util.http.HttpUtil;
import de.terrestris.shogun2.util.interceptor.InterceptorException;
import de.terrestris.shogun2.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shogun2.util.interceptor.OgcMessage;
import de.terrestris.shogun2.util.interceptor.OgcMessageDistributor;
import de.terrestris.shogun2.util.interceptor.OgcNaming;
import de.terrestris.shogun2.util.interceptor.OgcXmlUtil;
import de.terrestris.shogun2.util.model.Response;

/**
 *
 * @author Daniel Koch
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 *
 */
@Service
public class GeoServerInterceptorService {

	/**
	 * The Logger.
	 */
	private static final Logger LOG = Logger.getLogger(
			GeoServerInterceptorService.class);

	/**
	 * The autowired properties file containing the (application driven)
	 * GeoServer namespace - GeoServer BaseURI mapping, e.g.:
	 *
	 *   topp=http://localhost:8080/geoserver/topp/ows
	 *
	 */
	private Properties geoServerNameSpaces;

	/**
	 * An array of whitelisted Headers to forward within the Interceptor.
	 */
	private static final String[] FORWARD_HEADER_KEYS = new String[] {
		"Content-Type",
		"Content-Disposition",
		"geowebcache-cache-result",
		"geowebcache-crs",
		"geowebcache-gridset",
		"geowebcache-tile-bounds",
		"geowebcache-tile-index"
	};

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
	 * @param request
	 * @return
	 * @throws InterceptorException
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public Response interceptGeoServerRequest(HttpServletRequest request)
			throws InterceptorException, URISyntaxException,
			UnsupportedEncodingException, HttpException {

		// wrap the request, we want to manipulate it
		MutableHttpServletRequest mutableRequest =
				new MutableHttpServletRequest(request);

		// get the OGC request information (service, request, endPoint)
		OgcMessage message = getOgcRequest(mutableRequest);

		// get the GeoServer base URI by the provided request
		URI geoServerBaseUri = getGeoServerBaseURI(message);

		// set the GeoServer base URI to the (wrapped) request
		mutableRequest.setRequestURI(geoServerBaseUri);

		// intercept the request (if needed)
		mutableRequest = ogcMessageDistributor
				.distributeToRequestInterceptor(mutableRequest, message);

		// send the request
		// TODO: Move to global proxy class
		Response response = sendRequest(mutableRequest);

		// intercept the response (if needed)
		Response interceptedResponse = ogcMessageDistributor
				.distributeToResponeInterceptor(response, message);

		// finally filter the white-listed response headers
		// TODO: Move to global proxy class
		interceptedResponse.setHeaders(
				getHeadersToForward(interceptedResponse.getHeaders()));

		return interceptedResponse;
	}

	/**
	 *
	 * @param mutableRequest
	 * @return
	 * @throws InterceptorException
	 */
	private OgcMessage getOgcRequest(MutableHttpServletRequest mutableRequest)
			throws InterceptorException {

		OgcMessage ogcRequest = new OgcMessage();

		String requestService = getRequestParameterValue(
				mutableRequest, OgcNaming.PARAMETER_SERVICE);
		String requestOperation = getRequestParameterValue(
				mutableRequest, OgcNaming.PARAMETER_OPERATION);
		String requestLayer = getRequestParameterValue(
				mutableRequest, OgcNaming.PARAMETER_ENDPOINT);
		InterceptorRule mostSpecificRequestRule = getMostSpecificRule(requestService,
				requestOperation, requestLayer, "REQUEST");
		InterceptorRule mostSpecificResponseRule = getMostSpecificRule(requestService,
				requestOperation, requestLayer, "RESPONSE");

		if (StringUtils.isNoneEmpty(requestService)) {
			ogcRequest.setService(requestService);
		} else {
			LOG.debug("No service found.");
		}

		if (StringUtils.isNoneEmpty(requestOperation)) {
			ogcRequest.setOperation(requestOperation);
		} else {
			LOG.debug("No operation found.");
		}

		if (StringUtils.isNoneEmpty(requestLayer)) {
			ogcRequest.setEndPoint(requestLayer);
		} else {
			LOG.debug("No endPoint found.");
		}

		if (mostSpecificRequestRule != null) {
			ogcRequest.setRequestRule(mostSpecificRequestRule.getRule());
		} else {
			LOG.debug("No interceptor rule found for the request.");
		}

		if (mostSpecificResponseRule != null) {
			ogcRequest.setResponseRule(mostSpecificResponseRule.getRule());
		} else {
			LOG.debug("No interceptor rule found for the response.");
		}

		if (StringUtils.isEmpty(requestService) &&
				StringUtils.isEmpty(requestOperation) &&
				StringUtils.isEmpty(requestLayer)) {
			throw new InterceptorException("Couldn't find all required OGC " +
					"parameters (SERVICE, REQUEST, LAYER). Please check the " +
					"validity of the request.");
		}

		return ogcRequest;
	}

	/**
	 *
	 * @param requestService
	 * @param requestOperation
	 * @param requestLayer
	 * @return
	 */
	private InterceptorRule getMostSpecificRule(String requestService,
			String requestOperation, String requestEndPoint, String ruleEvent) {

		List<InterceptorRule> result;

		Map<String, String> filterMap = new HashMap<String, String>();

		filterMap.put("event", ruleEvent);

		filterMap.put("service", requestService);
		filterMap.put("operation", requestOperation);
		filterMap.put("endPoint", requestEndPoint);

		result = this.interceptorRuleService.findSpecificRule(filterMap);

		if (!result.isEmpty()) {
			LOG.debug(ruleEvent + ": Found Service, Operation and EndPoint " +
					"set in interceptor rule");
			return result.get(0);
		}

		filterMap.put("operation", null);

		result = this.interceptorRuleService.findSpecificRule(filterMap);

		if (!result.isEmpty()) {
			LOG.debug(ruleEvent + ": Found Service and EndPoint set in " +
					"interceptor rule");
			return result.get(0);
		}

		filterMap.put("operation", requestOperation);
		filterMap.put("endPoint", null);

		result = this.interceptorRuleService.findSpecificRule(filterMap);

		if (!result.isEmpty()) {
			LOG.debug(ruleEvent + ": Found Service and Operation set in " +
					"interceptor rule");
			return result.get(0);
		}

		filterMap.put("operation", null);
		filterMap.put("endPoint", null);

		result = this.interceptorRuleService.findSpecificRule(filterMap);

		if (!result.isEmpty()) {
			LOG.debug(ruleEvent + ": Found Service set in interceptor rule");
			return result.get(0);
		}

		return null;

	}

	/**
	 *
	 * @param mutableRequest
	 * @param key
	 * @return
	 * @throws InterceptorException
	 */
	private static String getRequestParameterValue(MutableHttpServletRequest mutableRequest,
			String[] keys) throws InterceptorException {

		String value = StringUtils.EMPTY;

		for (String key : keys) {
			value = getRequestParameterValue(mutableRequest, key);

			if (StringUtils.isNoneEmpty(value)) {
				break;
			}
		}

		return value;
	}

	/**
	 *
	 * @param mutableRequest
	 * @param key
	 * @return
	 * @throws InterceptorException
	 */
	private static String getRequestParameterValue(MutableHttpServletRequest mutableRequest,
			String key) throws InterceptorException {

		if (StringUtils.isEmpty(key)) {
			throw new InterceptorException("Missing parameter key");
		}

		String value = StringUtils.EMPTY;

		Map<String, String[]> queryParams = mutableRequest.getParameterMap();

		if (!queryParams.isEmpty()) {

			TreeMap<String, String[]> params = new TreeMap<String, String[]>(
					String.CASE_INSENSITIVE_ORDER);

			params.putAll(queryParams);

			if (params.containsKey(key)) {
				value = StringUtils.join(params.get(key), ";");
			}

		} else {

			String xml = OgcXmlUtil.getRequestBody(mutableRequest);

			if (!StringUtils.isEmpty(xml)) {

				Document document = OgcXmlUtil.getDocumentFromString(xml);

				if (key.equalsIgnoreCase(OgcNaming.PARAMETER_SERVICE)) {
					value = OgcXmlUtil.getPathInDocument(
							document, "/*/@service");
				} else if (key.equalsIgnoreCase(OgcNaming.PARAMETER_OPERATION)) {
					value = OgcXmlUtil.getPathInDocument(
							document, "name(/*)");

					if (value.contains(":")) {
						value = value.split(":")[1];
					}

				} else if (Arrays.asList(OgcNaming.PARAMETER_ENDPOINT).contains(key)) {
					value = OgcXmlUtil.getPathInDocument(
							document, "//TypeName/text()");
					if (StringUtils.isEmpty(value)) {
						value = OgcXmlUtil.getPathInDocument(document,
								"//@typeName");
					}
				}

			} else {
				LOG.error("No body found");
			}
		}

		return value;

	}

	/**
	 *
	 * @param params
	 * @return
	 */
	private static List<NameValuePair> createQueryParams(Map<String, String[]> params) {

		List<NameValuePair> queryParams = new ArrayList<NameValuePair>();

		for (Entry<String, String[]> param : params.entrySet()) {
			queryParams.add(new BasicNameValuePair(param.getKey(),
					StringUtils.join(param.getValue())));
		}

		return queryParams;
	}

	/**
	 *
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
	 *
	 * @param params
	 * @return
	 * @throws URISyntaxException
	 * @throws InterceptorException
	 */
	private URI getGeoServerBaseURIFromNameSpace(String geoServerNamespace)
			throws URISyntaxException, InterceptorException {

		URI uri = null;

		String geoServerUrl = this.geoServerNameSpaces.getProperty(
				geoServerNamespace);

		if (StringUtils.isEmpty(geoServerUrl)) {
			throw new InterceptorException("Couldn't detect GeoServer URI " +
					"from the given namespace");
		}

		URIBuilder builder = new URIBuilder(geoServerUrl);

		uri = builder.build();

		return uri;
	}

	/**
	 *
	 * @param message
	 * @throws URISyntaxException
	 * @throws InterceptorException
	 */
	private URI getGeoServerBaseURI(OgcMessage message) throws URISyntaxException,
			InterceptorException {

		// get the namespace from the qualified endpoint name
		String geoServerNamespace = getGeoServerNameSpace(message.getEndPoint());

		// set the GeoServer base URL
		URI geoServerBaseUri = getGeoServerBaseURIFromNameSpace(geoServerNamespace);

		return geoServerBaseUri;
	}

	/**
	 *
	 * @param qualifiedLayerName
	 * @return
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
	 *
	 * @param request
	 * @return
	 * @throws InterceptorException
	 * @throws HttpException
	 */
	private static Response sendRequest(MutableHttpServletRequest request)
			throws InterceptorException, HttpException {

		Response httpResponse = new Response();

		boolean getRequest = request.getMethod().equalsIgnoreCase("GET");
		boolean postRequest = request.getMethod().equalsIgnoreCase("POST");

		try {

			// get the request URI
			URI requestUri = new URI(request.getRequestURI());

			// get the query parameters provided by the GET/POST request and
			// convert to a list of NameValuePairs
			List<NameValuePair> queryParams = createQueryParams(
					request.getParameterMap());

			// append the given request parameters to the base URI
			URI fullRequestUri = getFullRequestURI(requestUri,
					queryParams);

			if (getRequest) {
				// if we're called via GET method

				// perform the request with the given parameters
				httpResponse = HttpUtil.get(fullRequestUri);

			} else if (postRequest) {
				// if we're called via POST method

				// get the request body if any
				String body = OgcXmlUtil.getRequestBody(request);

				if (!StringUtils.isEmpty(body)) {
					// we do have a POST with string data present

					// parse the content type of the request
					ContentType contentType = ContentType.parse(
							request.getContentType());

					// perform the request with the given parameters
					httpResponse = HttpUtil.post(fullRequestUri, body,
							contentType);
				} else {
					// perform the request with the given parameters
					httpResponse = HttpUtil.post(fullRequestUri, queryParams);
				}

			} else {
				// otherwise throw an exception
				throw new InterceptorException("Only GET or POST method "
						+ "is allowed");
			}

		} catch (URISyntaxException | UnsupportedEncodingException e) {
			LOG.error("Error while sending request: " + e.getMessage());
		}

		return httpResponse;

	}

	/**
	 * @return
	 * @throws UnsupportedEncodingException
	 *
	 */
	private static HttpHeaders getHeadersToForward(HttpHeaders headers)
			throws UnsupportedEncodingException {

		HttpHeaders responseHeaders = new HttpHeaders();

		if (headers == null) {
			LOG.debug("No headers found to forward!");
			return responseHeaders;
		}

		LOG.debug("Requested to filter the Headers to respond with:");

		for (Entry<String, List<String>> header : headers.entrySet()) {
			String headerKey = header.getKey();
			String headerVal = StringUtils.join(header.getValue(), ",");

			LOG.debug("  * Header: " + headerKey);

			if (Arrays.asList(FORWARD_HEADER_KEYS).contains(headerKey)) {

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
				LOG.debug("    > Forwarded");
			} else {
				LOG.debug("    > Skipped");
			}
		}

		return responseHeaders;
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
