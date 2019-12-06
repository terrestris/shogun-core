package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.util.http.HttpUtil;
import de.terrestris.shoguncore.util.model.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Simple HTTP Proxy service (forward proxy)
 *
 * @author Andre Henn
 * @author terrestris GmbH & co. KG
 */
@Service("httpProxyService")
public class HttpProxyService {

    /* +--------------------------------------------------------------------+ */
    /* | static errors and response entities                                | */
    /* +--------------------------------------------------------------------+ */
    public static final String ERR_MSG_400_NO_URL = "ERROR 400 (Bad Request):"
        + " The HttpProxyService could not determine a URL to proxy to.";

    /* +--------------------------------------------------------------------+ */
    /* | Generic constants                                                  | */
    /* +--------------------------------------------------------------------+ */
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
    /**
     * The LOGGER instance (that will be available in all subclasses)
     */
    private static final Logger LOG = getLogger(HttpProxyService.class);
    /**
     * Used to as content type for error messages if a request could not be
     * proxied.
     */
    private static final String CONTENT_TYPE_TEXT_PLAIN = MediaType.TEXT_PLAIN.toString();
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

    /**
     * The ports for http / https connections
     */
    private static final int HTTPS_PORT = 443;
    private static final int HTTP_PORT = 80;

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
        return doProxy(request, baseUrl, params, true);
    }

    /**
     * @param request
     * @param baseUrl
     * @param params
     * @param useWhitelist
     * @return
     */
    public ResponseEntity<?> doProxy(HttpServletRequest request, String baseUrl, Map<String, String> params,
                                     boolean useWhitelist) {
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

        if (useWhitelist) {
            // check if URI is contained in whitelist
            final boolean isInWhiteList = isInWhiteList(url);

            if (!isInWhiteList) {
                LOG.warn(ERR_MSG_502);
                return RESPONSE_502_BAD_GATEWAY;
            }
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
        for (Entry<String, String> entry : params.entrySet()) {
            uriBuilder.addParameter(entry.getKey(), entry.getValue());
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

        int portToTest = -1;
        if (port != -1) {
            portToTest = port;
        } else {
            portToTest = StringUtils.equalsIgnoreCase(protocol, "https") ? HTTPS_PORT : HTTP_PORT;
        }

        final int finalPortToTest = portToTest;
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
            final int portToTestAgainst = (whitelistPort != -1) ? whitelistPort : (StringUtils.equalsIgnoreCase(protocol, "https") ? HTTPS_PORT : HTTP_PORT);
            final boolean portIsMatching = portToTestAgainst == finalPortToTest;
            final boolean domainIsMatching = StringUtils.equalsIgnoreCase(host, whitelistHost) || StringUtils.endsWith(host, whitelistHost);
            return (portIsMatching && domainIsMatching);
        }).collect(Collectors.toList());
        boolean isAllowed = !matchingWhiteListEntries.isEmpty();

        return isAllowed;
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
