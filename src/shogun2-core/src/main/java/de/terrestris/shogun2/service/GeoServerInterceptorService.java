package de.terrestris.shogun2.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.model.interceptor.InterceptorRule;
import de.terrestris.shogun2.util.enumeration.HttpEnum;
import de.terrestris.shogun2.util.enumeration.OgcEnum;
import de.terrestris.shogun2.util.enumeration.OgcEnum.OperationType;
import de.terrestris.shogun2.util.enumeration.OgcEnum.ServiceType;
import de.terrestris.shogun2.util.http.HttpUtil;
import de.terrestris.shogun2.util.interceptor.InterceptorException;
import de.terrestris.shogun2.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shogun2.util.interceptor.OgcMessage;
import de.terrestris.shogun2.util.interceptor.OgcMessageDistributor;
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
		"Content-Language",
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
	 * @throws HttpException
	 * @throws IOException
	 */
	public Response interceptGeoServerRequest(HttpServletRequest request)
			throws InterceptorException, URISyntaxException,
			HttpException, IOException {

		// wrap the request, we want to manipulate it
		MutableHttpServletRequest mutableRequest =
				new MutableHttpServletRequest(request);

		// get the OGC message information (service, request, endPoint)
		OgcMessage message = getOgcMessage(mutableRequest);

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
				.distributeToResponseInterceptor(mutableRequest, response, message);

		// finally filter the white-listed response headers
		// TODO: Move to global proxy class
		HttpHeaders forwardingHeaders= getHeadersToForward(
				interceptedResponse.getHeaders()
			);
		interceptedResponse.setHeaders(forwardingHeaders);

		return interceptedResponse;
	}

	/**
	 *
	 * @param mutableRequest
	 * @return
	 * @throws InterceptorException
	 * @throws IOException
	 */
	private OgcMessage getOgcMessage(MutableHttpServletRequest mutableRequest)
			throws InterceptorException, IOException {

		LOG.debug("Building the OGC message from the given request.");

		OgcMessage ogcMessage = new OgcMessage();

		String requestService = MutableHttpServletRequest.getRequestParameterValue(
				mutableRequest, OgcEnum.Service.SERVICE.toString());
		String requestOperation = MutableHttpServletRequest.getRequestParameterValue(
				mutableRequest, OgcEnum.Operation.OPERATION.toString());
		String requestEndPoint = MutableHttpServletRequest.getRequestParameterValue(
				mutableRequest, OgcEnum.EndPoint.getAllValues());

		if (StringUtils.isEmpty(requestService) ||
				StringUtils.isEmpty(requestOperation) ||
				StringUtils.isEmpty(requestEndPoint)) {
			throw new InterceptorException("Couldn't find all required OGC " +
					"parameters (SERVICE, REQUEST, ENDPOINT). Please check the " +
					"validity of the request.");
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

		LOG.debug("Successfully build the OGC message: " + ogcMessage);

		return ogcMessage;
	}

	/**
	 *
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

		if (LogManager.getRootLogger().getLevel().equals(Level.TRACE)) {
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
		Predicate<InterceptorRule> condition = new Predicate<InterceptorRule>() {
			@Override
			public boolean evaluate(InterceptorRule rule) {
				// most specific: we have a rule with a matching endPoint
				//                and operation
				if (Objects.equals(rule.getEndPoint(), endPoint) &&
						Objects.equals(rule.getOperation(), operation)) {
					LOG.trace("  * " + rule + " is endPoint and operation specific.");
					return true;
				// operation specific: if we have a rule with no matching
				//                     endPoint, but a matching operation
				} else if (Objects.equals(rule.getEndPoint(), null) &&
						Objects.equals(rule.getOperation(), operation)) {
					LOG.trace("  * " + rule + " is operation specific.");
					return true;
				// service specific: if we have a rule with neither a matching
				//                   endPoint and service, but a matching service
				} else if (Objects.equals(rule.getEndPoint(), null) &&
						Objects.equals(rule.getOperation(), null) &&
						Objects.equals(rule.getService(), service)) {
					LOG.trace("  * " + rule + " rule is service specific.");
					return true;
				// no match at all
				} else {
					LOG.trace("  * " + rule + " has no match.");
					return false;
				}
			}
		};

		// filter the input rules to get the most specific one
		InterceptorRule mostSpecificRule = IterableUtils.find(
				interceptorRules, condition);

		if (interceptorRules.size() == 0) {
			LOG.error("Got no interceptor rules for this request/response. " +
					"Usually this should not happen as one has to define at " +
					"least the basic sets of rules (e.g. ALLOW all WMS " +
					"requests) when using the interceptor.");
			throw new InterceptorException("No interceptor rule found.");
		} else {
			LOG.debug("Identified the following rule as most the specific " +
					"one: " + mostSpecificRule);
		}

		return mostSpecificRule;
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
					StringUtils.join(param.getValue(), ",")));
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
	public URI getGeoServerBaseURIFromNameSpace(String geoServerNamespace)
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

		LOG.debug("Finding the GeoServer base URI by the provided EndPoint: " +
				message.getEndPoint());

		// get the namespace from the qualified endPoint name
		String geoServerNamespace = getGeoServerNameSpace(message.getEndPoint());

		LOG.debug("Found the following GeoServer namespace set in the "
				+ "EndPoint: " + geoServerNamespace);

		// set the GeoServer base URL
		URI geoServerBaseUri = getGeoServerBaseURIFromNameSpace(geoServerNamespace);

		LOG.debug("The corresponding GeoServer base URI is: " + geoServerBaseUri);

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

		String requestMethod = request.getMethod();
		boolean getRequest = "GET".equalsIgnoreCase(requestMethod);
		boolean postRequest = "POST".equalsIgnoreCase(requestMethod);

		try {

			// get the request URI
			URI requestUri = new URI(request.getRequestURI());

			// get the query parameters provided by the GET/POST request and
			// convert to a list of NameValuePairs
			List<NameValuePair> allQueryParams = createQueryParams(request.getParameterMap());

			// append the given request parameters to the base URI
			URI fullRequestUri = getFullRequestURI(requestUri, allQueryParams);

			if (getRequest) {
				// if we're called via GET method

				// perform the request with the given parameters
				httpResponse = HttpUtil.get(fullRequestUri);

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

					// perform the POST request to the URI with queryString and with the given body
					httpResponse = HttpUtil.post(requestUri, body, contentType);
				} else {

					// perform the POST request with the given name value pairs,
					httpResponse = HttpUtil.post(requestUri, allQueryParams);
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
	 * Returns a new URI with the passed queryString (e.g. foo=bar&baz=123) appended to the passed URI. Adjusted from
	 * http://stackoverflow.com/a/26177982.
	 *
	 * @param uri
	 * @param appendQuery
	 * @return
	 * @throws URISyntaxException
	 */
	public static URI appendQueryString(URI uri, String appendQuery) {
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
	 *
	 */
	private static HttpHeaders getHeadersToForward(HttpHeaders headers)
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
				LOG.trace("    > Forwarded");
			} else {
				LOG.trace("    > Skipped");
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
