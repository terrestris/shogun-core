package de.terrestris.shogun2.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import de.terrestris.shogun2.util.http.HttpUtil;
import de.terrestris.shogun2.util.model.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Simple HTTP Proxy service (forward proxy)
 *
 * @author Andre Henn
 * @author terrestris GmbH & co. KG
 */
@Service("httpProxyService")
public class HttpProxyService {

    /**
     * The LOGGER instance (that will be available in all subclasses)
     */
    private static final Logger LOG = Logger.getLogger(HttpProxyService.class);

    /* +--------------------------------------------------------------------+ */
    /* | Generic constants                                                  | */
    /* +--------------------------------------------------------------------+ */

    /**
     * Used to as content type for error messages if a request could not be
     * proxied.
     */
    private static final String CONTENT_TYPE_TEXT_PLAIN = MediaType.TEXT_PLAIN.toString();

    /* +--------------------------------------------------------------------+ */
    /* | static errors and response entities                                | */
    /* +--------------------------------------------------------------------+ */
    public static final String ERR_MSG_400_NO_URL = "ERROR 400 (Bad Request):"
        + " The HttpProxyService could not determine a URL to proxy to.";

    public static final String ERR_MSG_400_COMMON = "ERROR 400 (Bad Request):"
        + " Please check the log files for details.";

    public static final String ERR_MSG_404 = "ERROR 404 (Not found):"
        + " The HttpProxyService could not find the requested service.";

    public static final String ERR_MSG_405 = "ERROR 405: (Method Not Allowed):"
        + " The HttpProxyService does not support this request method.";

    public static final String ERR_MSG_500 = "ERROR 500 (Internal Error)"
        + " An internal error occured which prevented further processing.";

    public static final String ERR_MSG_502 = "ERROR 502 (Bad Gateway):"
        + " The HttpProxyService does not allow you to access that location.";

    private static final ResponseEntity<String> RESPONSE_400_BAD_REQUEST_COMMON =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_400_COMMON);

    private static final ResponseEntity<String> RESPONSE_400_BAD_REQUEST_NO_URL =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_400_NO_URL);

    private static final ResponseEntity<String> RESPONSE_404_NOT_FOUND =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_404);

    private static final ResponseEntity<String> RESPONSE_405_METHOD_NOT_ALLOWED =
        ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_405);

    private static final ResponseEntity<String> RESPONSE_500_INTERNAL_SERVER_ERROR =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_500);

    private static final ResponseEntity<String> RESPONSE_502_BAD_GATEWAY =
        ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .header("Content-Type", CONTENT_TYPE_TEXT_PLAIN)
            .body(ERR_MSG_502);

    /* +--------------------------------------------------------------------+ */
    /* | HTTP header stuff                                                  | */
    /* +--------------------------------------------------------------------+ */
    /**
     * Occurrences of the string UNQUOTED_SUBTYPE_GML will be replaced
     * with the quoted {@value #QUOTED_SUBTYPE_GML}.
     */
    private static final String UNQUOTED_SUBTYPE_GML = " subtype=gml/3.1.1";

    /**
     * This is the quoted replacement (QUOTED_SUBTYPE_GML) for the
     * string {@value #UNQUOTED_SUBTYPE_GML}.
     */
    private static final String QUOTED_SUBTYPE_GML = " subtype=\"gml/3.1.1\"";

    /**
     * Appended to {@link #JSON_CONTENT_TYPE_HEADER} and
     * {@link #CSV_CONTENT_TYPE_HEADER}
     */
    private static final String HEADER_SUFFIX_UTF8_CHARSET = "; charset=utf-8";

    /**
     * A normalized Content-Type-header {@value #JSON_CONTENT_TYPE_HEADER} as
     * replacement for content types that look like JSON (see
     * {@link #CONTENT_TYPE_JSON_HINTS}).
     */
    private static final String JSON_CONTENT_TYPE_HEADER = "application/json"
        + HEADER_SUFFIX_UTF8_CHARSET;

    /**
     * A normalized Content-Type-header {@value #CSV_CONTENT_TYPE_HEADER} as
     * replacement for content types that look like CSV (see
     * {@link #CONTENT_TYPE_CSV_HINTS}).
     */
    private static final String CSV_CONTENT_TYPE_HEADER = "text/csv"
        + HEADER_SUFFIX_UTF8_CHARSET;

    /**
     * The set of Content-Type-headers strings which we'll replace with
     * {@link #JSON_CONTENT_TYPE_HEADER}
     */
    private static final Set<String> CONTENT_TYPE_JSON_HINTS = new HashSet<>(
        Arrays.asList("json", "application/json", "text/json"));

    /**
     * The set of Content-Type-headers strings which we'll replace with
     * {@link #JSON_CONTENT_TYPE_HEADER}
     */
    private static final Set<String> CONTENT_TYPE_CSV_HINTS = new HashSet<>(
        Arrays.asList("text/csv"));

    /**
     * The set of header names which we'll ignore when returning a response. The
     * values herein are all lowercase.
     */
    private static final Set<String> LC_UNSUPPORTED_HEADERS = new HashSet<>(
        Arrays.asList("transfer-encoding"));

    /* +--------------------------------------------------------------------+ */
    /* | Autowired variables                                                | */
    /* +--------------------------------------------------------------------+ */
    /**
     * The list of whitelisted hosts
     */
    @Value("#{'${proxy.whitelist}'.split(',')}")
    private List<String> proxyWhiteList;

    /**
     * @param request
     * @param baseUrl
     * @param params
     * @return
     */
    public ResponseEntity<?> doProxy(HttpServletRequest request, String baseUrl, Map<String, String> params) {
        LOG.debug("Intercepting a request against service '" + baseUrl + "' with parameters: " + params);

        if (StringUtils.isEmpty(baseUrl) || request == null) {
            LOG.warn(ERR_MSG_400_NO_URL);
            return RESPONSE_400_BAD_REQUEST_NO_URL;
        }

        // transform to URL
        URL url;
        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException use) {
            LOG.error(RESPONSE_500_INTERNAL_SERVER_ERROR);
            return RESPONSE_500_INTERNAL_SERVER_ERROR;
        }

        // Could not parse URI properly
        if (url == null) {
            LOG.warn(ERR_MSG_404);
            return RESPONSE_404_NOT_FOUND;
        }

        // check if URI is contained in whitelist
        final boolean isInWhiteList = isInWhiteList(url);

        if (!isInWhiteList) {
            LOG.warn(ERR_MSG_502);
            return RESPONSE_502_BAD_GATEWAY;
        }

        // build request for params and baseUrl;
        try {
            url = buildUriWithParameters(url, params);
        } catch (URISyntaxException | MalformedURLException excep) {
            LOG.error(RESPONSE_500_INTERNAL_SERVER_ERROR);
            return RESPONSE_500_INTERNAL_SERVER_ERROR;
        }

        // Proxy the request
        Response response;
        if (HttpUtil.isHttpGetRequest(request)) {
            try {
                LOG.debug("Forwarding as GET to: " + url);
                response = HttpUtil.forwardGet(url.toURI(), request, false);
            } catch (URISyntaxException | HttpException e) {
                String errorMessage = "Error forwarding GET request: " + e.getMessage();
                LOG.error(errorMessage);
                return RESPONSE_400_BAD_REQUEST_COMMON;
            }
        } else if (HttpUtil.isHttpPostRequest(request)) {
            if (HttpUtil.isFormMultipartPost(request)) {
                try {
                    LOG.debug("Forwarding as form/multipart POST");
                    response = HttpUtil.forwardFormMultipartPost(url.toURI(), request, false);
                } catch (URISyntaxException | HttpException | IllegalStateException | IOException | ServletException e) {
                    String errorMessage = "Error forwarding form/multipart POST request: " + e.getMessage();
                    LOG.error(errorMessage);
                    return RESPONSE_400_BAD_REQUEST_COMMON;
                }
            } else {
                try {
                    LOG.debug("Forwarding as POST");
                    response = HttpUtil.forwardPost(url.toURI(), request, false);
                } catch (URISyntaxException | HttpException e) {
                    String errorMessage = "Error forwarding POST request: " + e.getMessage();
                    LOG.error(errorMessage);
                    return RESPONSE_400_BAD_REQUEST_COMMON;
                }
            }
        } else {
            LOG.error("Proxy does not yet support HTTP method: " + request.getMethod());
            return RESPONSE_405_METHOD_NOT_ALLOWED;
        }

        byte[] bytes = response.getBody();
        final HttpHeaders responseHeadersToForward = response.getHeaders();
        //getResponseHeadersToForward(response); // TODO adapt headers in the future!

        // LOG response headers
        Set<Entry<String, List<String>>> headerEntries = responseHeadersToForward.entrySet();
        for (Entry<String, List<String>> headerEntry : headerEntries) {
            String headerKey = headerEntry.getKey();
            List<String> headerValues = headerEntry.getValue();
            String joinedHeaderValues = StringUtils.join(headerValues, "; ");

            LOG.debug("Got the following response header: " + headerKey + "=" + joinedHeaderValues);
        }

        final HttpStatus responseHttpStatus = response.getStatusCode();

        return new ResponseEntity<>(bytes, responseHeadersToForward, responseHttpStatus);
    }

    /**
     * Helper method to build an {@link URL} from a baseUri and request parameters
     *
     * @param url    Base {@link URL}
     * @param params request parameters
     * @return URI
     */
    private URL buildUriWithParameters(URL url, Map<String, String> params) throws URISyntaxException, MalformedURLException {
        if (params == null || params.isEmpty()) {
            return url;
        }
        URIBuilder uriBuilder = new URIBuilder(url.toURI());
        for (String paramName : params.keySet()) {
            uriBuilder.addParameter(paramName, params.get(paramName));
        }
        return uriBuilder.build().toURL();
    }

    /**
     * Helper method to check whether the URI is contained in the host whitelist provided in list of whitelisted hosts
     *
     * @param url {@link URI} to check
     * @return true if contained, false otherwise
     */
    private boolean isInWhiteList(URL url) {
        final String host = url.getHost();
        final int port = url.getPort();
        final String protocol = url.getProtocol();

        final int portToTest = (port != -1) ? port : (StringUtils.equalsIgnoreCase(protocol, "https") ? 443 : 80);

        List<String> matchingWhiteListEntries = proxyWhiteList.stream().filter((String whitelistEntry) -> {
            String whitelistHost;
            int whitelistPort;
            if (StringUtils.contains(whitelistEntry, ":")) {
                whitelistHost = whitelistEntry.split(":")[0];
                whitelistPort = Integer.parseInt(whitelistEntry.split(":")[1]);
            } else {
                whitelistHost = whitelistEntry;
                whitelistPort = -1;
            }
            final int portToTestAgainst = (whitelistPort != -1) ? whitelistPort : (StringUtils.equalsIgnoreCase(protocol, "https") ? 443 : 80);
            final boolean portIsMatching = portToTestAgainst == portToTest;
            final boolean domainIsMatching = StringUtils.equalsIgnoreCase(host, whitelistHost) || StringUtils.endsWith(host, whitelistHost);
            return (portIsMatching && domainIsMatching);
        }).collect(Collectors.toList());
        boolean isAllowed = !matchingWhiteListEntries.isEmpty();

        return isAllowed;
    }

    /**
     * @param originalResponse
     * @return
     */
    private static HttpHeaders getHeadersToForward(HttpResponse originalResponse) {

        HttpHeaders responseHeaders = new HttpHeaders();

        if (originalResponse == null) {
            return responseHeaders;
        }

        // This is a fallback, we usually will overwrite this with s.th.
        // more specific from the response.
        responseHeaders.setContentType(new MediaType("text", "plain"));

        Header[] originalResponseHeaders = originalResponse.getAllHeaders();

        StringBuffer bufferHeaders = new StringBuffer();

        for (Header header : originalResponseHeaders) {
            String headerName = header.getName();
            String headerVal = header.getValue();

            if (isUnsupportedHeader(headerName)) {
                LOG.debug("Unsupported header '" + headerName + "' found "
                    + " and ignored");
            } else {
                headerVal = fixUpHeaderValue(headerName, headerVal);

                // now set the header in the return headers
                responseHeaders.set(headerName, headerVal);
                bufferHeaders.append(headerName + "=" + headerVal + ", ");
            }

        }

        if (responseHeaders.size() > 1) {
            LOG.debug("List of headers for the final response of this request: "
                + bufferHeaders.toString().replaceAll("(,\\s*)$", ""));
        } else {
            LOG.debug("No specific headers to forward, "
                + "setting 'ContentType: text/plain' as fallback");
        }
        return responseHeaders;
    }

    /**
     * @param headerName
     * @return
     */
    private static boolean isUnsupportedHeader(String headerName) {
        return !isSupportedHeader(headerName);
    }

    /**
     * @param headerName
     * @return
     */
    private static boolean isSupportedHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        if (LC_UNSUPPORTED_HEADERS.contains(headerName.toLowerCase())) {
            return false;
        }
        return true;
    }

    /**
     * @param headerName
     * @param headerVal
     * @return
     */
    private static String fixUpHeaderValue(String headerName, String headerVal) {
        if (headerName == null || headerVal == null) {
            return null;
        }

        String logPrefix = "Header '" + headerName + "' has a value '"
            + headerVal + "'" + " which seems incorrect. ";

        String fixedHeaderVal = headerVal;

        String lowercaseHeaderVal = headerVal.toLowerCase().trim();

        if (lowercaseHeaderVal.contains("subtype")) {
            LOG.debug(logPrefix + " Quoting subtype to fix it.");
            fixedHeaderVal = headerVal.replace(UNQUOTED_SUBTYPE_GML,
                QUOTED_SUBTYPE_GML);
        } else if (CONTENT_TYPE_JSON_HINTS.contains(lowercaseHeaderVal)) {
            LOG.debug(logPrefix + " Using value '"
                + JSON_CONTENT_TYPE_HEADER + "'.");
            fixedHeaderVal = JSON_CONTENT_TYPE_HEADER;
        } else if (CONTENT_TYPE_CSV_HINTS.contains(lowercaseHeaderVal)) {
            LOG.debug(logPrefix + " Using value '"
                + JSON_CONTENT_TYPE_HEADER + "'.");
            fixedHeaderVal = CSV_CONTENT_TYPE_HEADER;
        }

        return fixedHeaderVal;
    }

    /**
     * The setter for proxy whitelist
     *
     * @param proxyWhiteList The {@link List} containing {@link String}s of whitelisted hosts
     */
    public void setProxyWhiteList(List<String> proxyWhiteList) {
        this.proxyWhiteList = proxyWhiteList;
    }
}
