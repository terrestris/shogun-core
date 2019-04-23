package de.terrestris.shoguncore.util.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import de.terrestris.shoguncore.util.model.Response;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author Daniel Koch
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 */
@Component
public class HttpUtil {

    /**
     * The Logger.
     */
    private static final Logger LOG = getLogger(HttpUtil.class);

    /**
     * The timeout for all outgoing HTTP connections.
     */
    private static int httpTimeout;

    /**
     * The default timeout given by the config beans.
     */
    private static int defaultHttpTimeout;

    /**
     * The name of the 'authorization' header
     */
    private static String AUTHORIZATION_HEADER = "authorization";

    /**
     * Performs an HTTP GET on the given URL <i>without authentication</i>
     *
     * @param url The URL to connect to.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response get(String url) throws URISyntaxException, HttpException {
        return send(new HttpGet(url), null, null);
    }

    /**
     * Performs an HTTP GET on the given URL <i>without authentication</i> and
     * additional HTTP request headers.
     *
     * @param url            The URL to connect to.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response get(String url, Header[] requestHeaders)
        throws URISyntaxException, HttpException {
        return send(new HttpGet(url), null, requestHeaders);
    }

    /**
     * Performs an HTTP GET on the given URL.
     *
     * @param url         The URL to connect to.
     * @param credentials instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response get(String url, Credentials credentials)
        throws URISyntaxException, HttpException {
        return send(new HttpGet(url), credentials, null);
    }

    /**
     * Performs an HTTP GET on the given URL.
     *
     * @param url            The URL to connect to.
     * @param credentials    instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response get(String url, Credentials credentials, Header[] requestHeaders)
        throws URISyntaxException, HttpException {
        return send(new HttpGet(url), credentials, requestHeaders);
    }

    /**
     * Performs an HTTP GET on the given URL
     *
     * @param url      The URL to connect to.
     * @param username Credentials - username
     * @param password Credentials - password
     * @throws HttpException
     * @throws URISyntaxException
     */
    public static Response get(String url, String username, String password) throws URISyntaxException, HttpException {
        return send(new HttpGet(url), new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP GET on the given URL
     *
     * @param url            The URL to connect to.
     * @param username       Credentials - username
     * @param password       Credentials - password
     * @param requestHeaders Additional HTTP headers added to the request
     * @throws HttpException
     * @throws URISyntaxException
     */
    public static Response get(String url, String username, String password, Header[] requestHeaders)
        throws URISyntaxException, HttpException {
        return send(new HttpGet(url), new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP GET on the given URI.
     * No credentials needed.
     *
     * @param uri The URI to connect to.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response get(URI uri) throws URISyntaxException, HttpException {
        return send(new HttpGet(uri), null, null);
    }

    /**
     * Performs an HTTP GET on the given URI.
     * No credentials needed.
     *
     * @param uri            The URI to connect to.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response get(URI uri, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return send(new HttpGet(uri), null, requestHeaders);
    }

    /**
     * Performs an HTTP GET on the given URI.
     * Basic auth is used if both username and password are not null.
     *
     * @param uri      The URI to connect to.
     * @param username Credentials - username
     * @param password Credentials - password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response get(URI uri, String username, String password)
        throws URISyntaxException, HttpException {
        return send(new HttpGet(uri), new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP GET on the given URI.
     * Basic auth is used if both username and password are not null.
     *
     * @param uri            The URI to connect to.
     * @param username       Credentials - username
     * @param password       Credentials - password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response get(URI uri, String username, String password, Header[] requestHeaders)
        throws URISyntaxException, HttpException {
        return send(new HttpGet(uri), new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP GET on the given URI.
     * Basic auth is used if both username and pw are not null.
     *
     * @param uri         The URI to connect to.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response get(URI uri, Credentials credentials)
        throws URISyntaxException, HttpException {
        return send(new HttpGet(uri), credentials, null);
    }

    /**
     * Performs an HTTP GET on the given URI.
     * Basic auth is used if both username and pw are not null.
     *
     * @param uri            The URI to connect to.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response get(URI uri, Credentials credentials, Header[] requestHeaders)
        throws URISyntaxException, HttpException {
        return send(new HttpGet(uri), credentials, requestHeaders);
    }

    /**
     * Forward GET request to uri based on given request
     *
     * @param uri            uri The URI to forward to.
     * @param request        The original {@link HttpServletRequest}
     * @param forwardHeaders Should headers of request should be forwarded
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response forwardGet(URI uri, HttpServletRequest request, boolean forwardHeaders)
        throws URISyntaxException, HttpException {

        Header[] headersToForward = null;

        if (request != null && forwardHeaders) {
            headersToForward = HttpUtil.getHeadersFromRequest(request);
        }

        return send(new HttpGet(uri), null, headersToForward);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url The URL to connect to.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), new ArrayList<NameValuePair>(), null, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url            The URL to connect to.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, Header[] requestHeaders)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), new ArrayList<NameValuePair>(), null, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if both and password are not null.
     *
     * @param url      The URI to connect to as String.
     * @param username Credentials - username
     * @param password Credentials - password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, String password, String username)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), new ArrayList<NameValuePair>(), new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if both and password are not null.
     *
     * @param url            The URI to connect to as String.
     * @param username       Credentials - username
     * @param password       Credentials - password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, String password, String username, Header[] requestHeaders)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), new ArrayList<NameValuePair>(), new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if credentials object is not null
     *
     * @param url         The URL to connect to.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, Credentials credentials)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), new ArrayList<NameValuePair>(), credentials, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if credentials object is not null
     *
     * @param url            The URL to connect to.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, Credentials credentials, Header[] requestHeaders)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), new ArrayList<NameValuePair>(), credentials, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URI.
     *
     * @param uri The URI to connect to.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), new ArrayList<NameValuePair>(), null, null);
    }

    /**
     * Performs an HTTP POST on the given URI.
     *
     * @param uri            The URI to connect to.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, Header[] requestHeaders)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), new ArrayList<NameValuePair>(), null, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URI.
     * Basic auth is used if both and password are not null.
     *
     * @param uri      The URI to connect to.
     * @param username Credentials - username
     * @param password Credentials - password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, String username, String password)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), new ArrayList<NameValuePair>(), new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP POST on the given URI.
     * Basic auth is used if both and password are not null.
     *
     * @param uri            The URI to connect to.
     * @param username       Credentials - username
     * @param password       Credentials - password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, String username, String password, Header[] requestHeaders)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), new ArrayList<NameValuePair>(), new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URI.
     * Basic auth is used if credentials object is not null
     *
     * @param uri         The URI to connect to.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, Credentials credentials)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), new ArrayList<NameValuePair>(), credentials, null);
    }

    /**
     * Performs an HTTP POST on the given URI.
     * Basic auth is used if credentials object is not null
     *
     * @param uri            The URI to connect to.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, Credentials credentials, Header[] requestHeaders)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), new ArrayList<NameValuePair>(), credentials, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url         The URL to connect to.
     * @param queryParams The list of NameValuePairs.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, List<NameValuePair> queryParams)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), queryParams, null, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url            The URL to connect to.
     * @param queryParams    The list of NameValuePairs.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, List<NameValuePair> queryParams, Header[] requestHeaders)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), queryParams, null, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if both and password are not null.
     *
     * @param url         The URL to connect to.
     * @param queryParams The list of NameValuePairs.
     * @param username    Credentials - username
     * @param password    Credentials - password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, List<NameValuePair> queryParams, String username, String password) throws URISyntaxException,
        UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), queryParams, new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if both and password are not null.
     *
     * @param url            The URL to connect to.
     * @param queryParams    The list of NameValuePairs.
     * @param username       Credentials - username
     * @param password       Credentials - password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, List<NameValuePair> queryParams, String username, String password, Header[] requestHeaders) throws URISyntaxException,
        UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), queryParams, new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url         The URL to connect to.
     * @param queryParams The list of NameValuePairs.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, List<NameValuePair> queryParams, Credentials credentials) throws URISyntaxException,
        UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), queryParams, credentials, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url            The URL to connect to.
     * @param queryParams    The list of NameValuePairs.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(String url, List<NameValuePair> queryParams, Credentials credentials, Header[] requestHeaders) throws URISyntaxException,
        UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(url), queryParams, credentials, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URI.
     *
     * @param uri         The URI to connect to.
     * @param queryParams The list of NameValuePairs.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, List<NameValuePair> queryParams)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), queryParams, null, null);
    }

    /**
     * Performs an HTTP POST on the given URI.
     *
     * @param uri            The URI to connect to.
     * @param queryParams    The list of NameValuePairs.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, List<NameValuePair> queryParams, Header[] requestHeaders)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), queryParams, null, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URI.
     * Basic auth is used if both and password are not null.
     *
     * @param uri         The URI to connect to.
     * @param queryParams The list of NameValuePairs.
     * @param username    Credentials - username
     * @param password    Credentials - password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, List<NameValuePair> queryParams, String username, String password) throws URISyntaxException,
        UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), queryParams, new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP POST on the given URI.
     * Basic auth is used if both and password are not null.
     *
     * @param uri            The URI to connect to.
     * @param queryParams    The list of NameValuePairs.
     * @param username       Credentials - username
     * @param password       Credentials - password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, List<NameValuePair> queryParams, String username, String password, Header[] requestHeaders) throws URISyntaxException,
        UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), queryParams, new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URI.
     *
     * @param uri         The URI to connect to.
     * @param queryParams The list of NameValuePairs.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, List<NameValuePair> queryParams, Credentials credentials) throws URISyntaxException,
        UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), queryParams, credentials, null);
    }

    /**
     * Performs an HTTP POST on the given URI.
     *
     * @param uri            The URI to connect to.
     * @param queryParams    The list of NameValuePairs.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    public static Response post(URI uri, List<NameValuePair> queryParams, Credentials credentials, Header[] requestHeaders) throws URISyntaxException,
        UnsupportedEncodingException, HttpException {
        return postParams(new HttpPost(uri), queryParams, credentials, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url         The URL to connect to.
     * @param body        The POST body.
     * @param contentType The ContentType of the POST body.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, String body, ContentType contentType)
        throws URISyntaxException, HttpException {
        return postBody(new HttpPost(url), body, contentType, null, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url            The URL to connect to.
     * @param body           The POST body.
     * @param contentType    The ContentType of the POST body.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, String body, ContentType contentType, Header[] requestHeaders)
        throws URISyntaxException, HttpException {
        return postBody(new HttpPost(url), body, contentType, null, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used is both username and password are not null
     *
     * @param url         The URL to connect to.
     * @param body        The POST body.
     * @param contentType The ContentType of the POST body
     * @param username    Credentials - username
     * @param password    Credentials - password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, String body, ContentType contentType, String username, String password) throws URISyntaxException, HttpException {
        return postBody(new HttpPost(url), body, contentType, new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used is both username and password are not null
     *
     * @param url            The URL to connect to.
     * @param body           The POST body.
     * @param contentType    The ContentType of the POST body
     * @param username       Credentials - username
     * @param password       Credentials - password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, String body, ContentType contentType, String username, String password, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return postBody(new HttpPost(url), body, contentType, new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url         The URL to connect to.
     * @param body        The POST body.
     * @param contentType The ContentType of the POST body.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, String body, ContentType contentType,
                                Credentials credentials) throws URISyntaxException, HttpException {
        return postBody(new HttpPost(url), body, contentType, credentials, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url            The URL to connect to.
     * @param body           The POST body.
     * @param contentType    The ContentType of the POST body.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, String body, ContentType contentType,
                                Credentials credentials, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return postBody(new HttpPost(url), body, contentType, credentials, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param uri         The URI to connect to.
     * @param body        The POST body.
     * @param contentType The ContentType of the POST body.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, String body, ContentType contentType)
        throws URISyntaxException, HttpException {
        return postBody(new HttpPost(uri), body, contentType, null, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param uri            The URI to connect to.
     * @param body           The POST body.
     * @param contentType    The ContentType of the POST body.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, String body, ContentType contentType, Header[] requestHeaders)
        throws URISyntaxException, HttpException {
        return postBody(new HttpPost(uri), body, contentType, null, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if both username and password are not null
     *
     * @param uri         The URI to connect to.
     * @param body        The POST body.
     * @param contentType The ContentType of the POST body.
     * @param username    Credentials - username
     * @param password    Credentials - password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, String body, ContentType contentType,
                                String username, String password) throws URISyntaxException, HttpException {
        return postBody(new HttpPost(uri), body, contentType, new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if both username and password are not null
     *
     * @param uri            The URI to connect to.
     * @param body           The POST body.
     * @param contentType    The ContentType of the POST body.
     * @param username       Credentials - username
     * @param password       Credentials - password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, String body, ContentType contentType,
                                String username, String password, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return postBody(new HttpPost(uri), body, contentType, new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param uri         The URI to connect to.
     * @param body        The POST body.
     * @param contentType The ContentType of the POST body.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, String body, ContentType contentType,
                                Credentials credentials) throws URISyntaxException, HttpException {
        return postBody(new HttpPost(uri), body, contentType, credentials, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param uri            The URI to connect to.
     * @param body           The POST body.
     * @param contentType    The ContentType of the POST body.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, String body, ContentType contentType,
                                Credentials credentials, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return postBody(new HttpPost(uri), body, contentType, credentials, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url  The URL to connect to.
     * @param file The file to send as MultiPartFile.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, File file) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(url), new FileBody(file), null, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param url            The URL to connect to.
     * @param file           The file to send as MultiPartFile.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, File file, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(url), new FileBody(file), null, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if both username and password are not null
     *
     * @param url      The URL to connect to.
     * @param file     The file to send as MultiPartFile.
     * @param username username
     * @param password password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, File file, String username, String password) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(url), new FileBody(file), new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if both username and password are not null
     *
     * @param url            The URL to connect to.
     * @param file           The file to send as MultiPartFile.
     * @param username       username
     * @param password       password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, File file, String username, String password, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(url), new FileBody(file), new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if credentials object is not null.
     *
     * @param url         The URL to connect to.
     * @param file        The file to send as MultiPartFile.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, File file, Credentials credentials) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(url), new FileBody(file), credentials, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if credentials object is not null.
     *
     * @param url            The URL to connect to.
     * @param file           The file to send as MultiPartFile.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(String url, File file, Credentials credentials, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(url), new FileBody(file), credentials, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param uri  The URI to connect to.
     * @param file The file to send as MultiPartFile.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, File file) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(uri), new FileBody(file), null, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     *
     * @param uri            The URI to connect to.
     * @param file           The file to send as MultiPartFile.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, File file, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(uri), new FileBody(file), null, requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if both username and password are not null.
     *
     * @param uri      The URI to connect to.
     * @param file     The file to send as MultiPartFile.
     * @param username username
     * @param password password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, File file, String username, String password) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(uri), new FileBody(file), new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if both username and password are not null.
     *
     * @param uri            The URI to connect to.
     * @param file           The file to send as MultiPartFile.
     * @param username       username
     * @param password       password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, File file, String username, String password, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(uri), new FileBody(file), new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if credentials object is not null.
     *
     * @param uri         The URI to connect to.
     * @param file        The file to send as MultiPartFile.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, File file, Credentials credentials) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(uri), new FileBody(file), credentials, null);
    }

    /**
     * Performs an HTTP POST on the given URL.
     * Basic auth is used if credentials object is not null.
     *
     * @param uri            The URI to connect to.
     * @param file           The file to send as MultiPartFile.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response post(URI uri, File file, Credentials credentials, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return postMultiPart(new HttpPost(uri), new FileBody(file), credentials, requestHeaders);
    }

    /**
     * Forward FormMultipartPost (HTTP POST) to uri based on given request
     *
     * @param uri            uri The URI to forward to.
     * @param request        The original {@link HttpServletRequest}
     * @param forwardHeaders Should headers of request should be forwarded
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     * @throws IllegalStateException
     * @throws IOException
     * @throws ServletException
     */
    public static Response forwardFormMultipartPost(URI uri, HttpServletRequest request, boolean forwardHeaders)
        throws URISyntaxException, HttpException, IllegalStateException, IOException, ServletException {
        Header[] headersToForward = null;

        if (request != null && forwardHeaders) {
            headersToForward = HttpUtil.getHeadersFromRequest(request);
        }

        // remove content headers as http client lib will care about this
        // when entity is set on the httpPost instance in the postMultiPart method
        headersToForward = removeHeaders(headersToForward, new String[]{"content-length", "content-type"});

        Collection<Part> parts = request.getParts();

        return HttpUtil.postMultiPart(uri, parts, headersToForward);
    }

    /**
     * @param uri              The URI to POSt to
     * @param parts            {@link Part}s of FormMultipartRequest
     * @param headersToForward Should headers of request should be forwarded
     * @return The HTTP response as Response object.
     * @throws IOException
     * @throws URISyntaxException
     * @throws HttpException
     */
    private static Response postMultiPart(URI uri, Collection<Part> parts, Header[] headersToForward) throws IOException, URISyntaxException, HttpException {

        HttpPost httpPost = new HttpPost(uri);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        for (Part part : parts) {
            String name = part.getName();
            String fileName = part.getSubmittedFileName();

            // we should use a ByteArrayBody instead of InputStreamBody!
            // when using InputStreamBody, the HttpClient lib will use chunked encoding,
            // which is not supported by all servers.
            // by using the ByteArrayBody, the size of the content is known and the lib
            // will NOT use chunked encoding, but add a content-length header instead.
            byte[] data = IOUtils.toByteArray(part.getInputStream());
            final ContentType contentType = ContentType.create(part.getContentType());

            ByteArrayBody byteArrayBody = new ByteArrayBody(data, contentType, fileName);

            // add the part
            builder.addPart(name, byteArrayBody);

            LOG.debug("Add a form/multipart part with name '" + name + "', content type '" + contentType.getMimeType() + "' and size "
                + part.getSize());
        }

        HttpEntity multiPartEntity = builder.build();

        httpPost.setEntity(multiPartEntity);

        return send(httpPost, null, headersToForward);
    }

    /**
     * @param httpRequest
     * @param file
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    private static Response postMultiPart(HttpPost httpRequest, FileBody file, Credentials credentials, Header[] requestHeaders) throws URISyntaxException, HttpException {

        HttpEntity multiPartEntity = MultipartEntityBuilder.create()
            .addPart("file", file)
            .build();
        httpRequest.setEntity(multiPartEntity);

        return send(httpRequest, credentials, requestHeaders);
    }

    /**
     * @param httpRequest
     * @param body
     * @param contentType
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    private static Response postBody(HttpPost httpRequest, String body, ContentType contentType, Credentials credentials, Header[] requestHeaders)
        throws URISyntaxException, HttpException {

        StringEntity stringEntity = new StringEntity(body, contentType);
        stringEntity.setChunked(true);
        httpRequest.setEntity(stringEntity);

        return send(httpRequest, credentials, requestHeaders);
    }

    /**
     * @param httpRequest
     * @param queryParams
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws HttpException
     */
    private static Response postParams(HttpPost httpRequest, List<NameValuePair> queryParams, Credentials credentials, Header[] requestHeaders)
        throws URISyntaxException, UnsupportedEncodingException, HttpException {

        if (!queryParams.isEmpty()) {
            HttpEntity httpEntity = new UrlEncodedFormEntity(queryParams);
            httpRequest.setEntity(httpEntity);
        }

        return send(httpRequest, credentials, requestHeaders);
    }

    /**
     * Forward POST to uri based on given request
     *
     * @param uri            uri The URI to forward to.
     * @param request        The original {@link HttpServletRequest}
     * @param forwardHeaders Should headers of request should be forwarded
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response forwardPost(URI uri, HttpServletRequest request, boolean forwardHeaders) throws URISyntaxException, HttpException {
        Header[] headersToForward = null;
        if (request != null && forwardHeaders) {
            headersToForward = HttpUtil.getHeadersFromRequest(request);
        }

        String ctString = request.getContentType();
        ContentType ct = ContentType.parse(ctString);
        String body = getRequestBody(request);

        return HttpUtil.postBody(new HttpPost(uri), body, ct, null, headersToForward);
    }


    /**
     * Perform HTTP PUT with empty body
     *
     * @param uri
     * @return
     * @throws HttpException
     * @throws URISyntaxException
     */
    public static Response put(URI uri) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), null, null, null, null);
    }

    /**
     * Perform HTTP PUT with empty body
     *
     * @param uri
     * @param requestHeaders Additional HTTP headers added to the request
     * @return
     * @throws HttpException
     * @throws URISyntaxException
     */
    public static Response put(URI uri, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), null, null, null, requestHeaders);
    }

    /**
     * Perform HTTP PUT with empty body
     *
     * @param uriString
     * @return
     * @throws HttpException
     * @throws URISyntaxException
     */
    public static Response put(String uriString) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), null, null, null, null);
    }

    /**
     * Perform HTTP PUT with empty body
     *
     * @param uriString
     * @param requestHeaders Additional HTTP headers added to the request
     * @return
     * @throws HttpException
     * @throws URISyntaxException
     */
    public static Response put(String uriString, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), null, null, null, requestHeaders);
    }

    /**
     * Perform HTTP PUT with empty body
     * Basic auth will be used if both username and password are not null
     *
     * @param uri      URI to connect to
     * @param username username
     * @param password password
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(URI uri, String username, String password) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), null, null, new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Perform HTTP PUT with empty body
     * Basic auth will be used if both username and password are not null
     *
     * @param uri            URI to connect to
     * @param username       username
     * @param password       password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(URI uri, String username, String password, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), null, null, new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Perform HTTP PUT with empty body
     *
     * @param uri         URI to connect to
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(URI uri, Credentials credentials) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), null, null, credentials, null);
    }

    /**
     * Perform HTTP PUT with empty body
     *
     * @param uri            URI to connect to
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(URI uri, Credentials credentials, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), null, null, credentials, requestHeaders);
    }

    /**
     * Perform HTTP PUT with empty body
     *
     * @param uriString String representing the URI to connect to
     * @param username  username
     * @param password  password
     * @return
     * @throws HttpException
     * @throws URISyntaxException
     */
    public static Response put(String uriString, String username, String password) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), null, null, new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Perform HTTP PUT with empty body
     *
     * @param uriString      String representing the URI to connect to
     * @param username       username
     * @param password       password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return
     * @throws HttpException
     * @throws URISyntaxException
     */
    public static Response put(String uriString, String username, String password, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), null, null, new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Perform HTTP PUT with empty body
     *
     * @param uriString   String representing the URI to connect to
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return
     * @throws HttpException
     * @throws URISyntaxException
     */
    public static Response put(String uriString, Credentials credentials) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), null, null, credentials, null);
    }

    /**
     * Perform HTTP PUT with empty body
     *
     * @param uriString      String representing the URI to connect to
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return
     * @throws HttpException
     * @throws URISyntaxException
     */
    public static Response put(String uriString, Credentials credentials, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), null, null, credentials, requestHeaders);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     * (without BasicAuth)
     *
     * @param uriString   String representing the URI to connect to
     * @param body
     * @param contentType
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(String uriString, String body, ContentType contentType) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), body, contentType, null, null);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     * (without BasicAuth)
     *
     * @param uriString      String representing the URI to connect to
     * @param body
     * @param contentType
     * @param requestHeaders Additional HTTP headers added to the request
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(String uriString, String body, ContentType contentType, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), body, contentType, null, requestHeaders);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     *
     * @param uriString   String representing the URI to connect to
     * @param body
     * @param contentType
     * @param username
     * @param password
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(String uriString, String body, ContentType contentType, String username, String password) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), body, contentType, new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     *
     * @param uriString      String representing the URI to connect to
     * @param body
     * @param contentType
     * @param username
     * @param password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(String uriString, String body, ContentType contentType, String username, String password, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), body, contentType, new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     *
     * @param uriString
     * @param body
     * @param contentType
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(String uriString, String body, ContentType contentType, Credentials credentials) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), body, contentType, credentials, null);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     *
     * @param uriString
     * @param body
     * @param contentType
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(String uriString, String body, ContentType contentType, Credentials credentials, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uriString), body, contentType, credentials, requestHeaders);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     * without BasicAuth
     *
     * @param uri
     * @param body
     * @param contentType
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(URI uri, String body, ContentType contentType) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), body, contentType, null, null);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     * without BasicAuth
     *
     * @param uri
     * @param body
     * @param contentType
     * @param requestHeaders Additional HTTP headers added to the request
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(URI uri, String body, ContentType contentType, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), body, contentType, null, requestHeaders);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     *
     * @param uri
     * @param body
     * @param contentType
     * @param username
     * @param password
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(URI uri, String body, ContentType contentType, String username, String password) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), body, contentType, new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     *
     * @param uri
     * @param body
     * @param contentType
     * @param username
     * @param password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(URI uri, String body, ContentType contentType, String username, String password, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), body, contentType, new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     *
     * @param uri
     * @param body
     * @param contentType
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(URI uri, String body, ContentType contentType, Credentials credentials) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), body, contentType, credentials, null);
    }

    /**
     * Performs an HTTP PUT on the given URL.
     *
     * @param uri
     * @param body
     * @param contentType
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response put(URI uri, String body, ContentType contentType, Credentials credentials, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return putBody(new HttpPut(uri), body, contentType, credentials, requestHeaders);
    }

    /**
     * Performs an HTTP DELETE on the given URL.
     *
     * @param url The URL to connect to.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(String url) throws URISyntaxException, HttpException {
        return send(new HttpDelete(url), null, null);
    }

    /**
     * Performs an HTTP DELETE on the given URL.
     *
     * @param url            The URL to connect to.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(String url, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return send(new HttpDelete(url), null, requestHeaders);
    }

    /**
     * Performs an HTTP DELETE on the given URL.
     *
     * @param url      The URL to connect to.
     * @param username username
     * @param password password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(String url, String username, String password)
        throws URISyntaxException, HttpException {
        return send(new HttpDelete(url), new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP DELETE on the given URL.
     *
     * @param url            The URL to connect to.
     * @param username       username
     * @param password       password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(String url, String username, String password, Header[] requestHeaders)
        throws URISyntaxException, HttpException {
        return send(new HttpDelete(url), new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP DELETE on the given URL.
     *
     * @param url         The URL to connect to.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(String url, Credentials credentials)
        throws URISyntaxException, HttpException {
        return send(new HttpDelete(url), credentials, null);
    }

    /**
     * Performs an HTTP DELETE on the given URL.
     *
     * @param url            The URL to connect to.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(String url, Credentials credentials, Header[] requestHeaders)
        throws URISyntaxException, HttpException {
        return send(new HttpDelete(url), credentials, requestHeaders);
    }

    /**
     * Performs an HTTP DELETE on the given URI.
     *
     * @param uri The URI to connect to.
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(URI uri) throws URISyntaxException, HttpException {
        return send(new HttpDelete(uri), null, null);
    }

    /**
     * Performs an HTTP DELETE on the given URI.
     *
     * @param uri            The URI to connect to.
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(URI uri, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return send(new HttpDelete(uri), null, requestHeaders);
    }

    /**
     * Performs an HTTP DELETE on the given URI.
     * Basic auth is used if both username and pw are not null.
     *
     * @param uri      The URI to connect to.
     * @param username username
     * @param password password
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(URI uri, String username, String password) throws URISyntaxException, HttpException {
        return send(new HttpDelete(uri), new UsernamePasswordCredentials(username, password), null);
    }

    /**
     * Performs an HTTP DELETE on the given URI.
     * Basic auth is used if both username and pw are not null.
     *
     * @param uri            The URI to connect to.
     * @param username       username
     * @param password       password
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(URI uri, String username, String password, Header[] requestHeaders) throws URISyntaxException, HttpException {
        return send(new HttpDelete(uri), new UsernamePasswordCredentials(username, password), requestHeaders);
    }

    /**
     * Performs an HTTP DELETE on the given URI.
     * Basic auth is used if both username and pw are not null.
     *
     * @param uri         The URI to connect to.
     * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(URI uri, Credentials credentials)
        throws URISyntaxException, HttpException {
        return send(new HttpDelete(uri), credentials, null);
    }

    /**
     * Performs an HTTP DELETE on the given URI.
     * Basic auth is used if both username and pw are not null.
     *
     * @param uri            The URI to connect to.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    public static Response delete(URI uri, Credentials credentials, Header[] requestHeaders)
        throws URISyntaxException, HttpException {
        return send(new HttpDelete(uri), credentials, requestHeaders);
    }

    /**
     * @param httpRequest
     * @param body
     * @param contentType
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    private static Response putBody(HttpPut httpRequest, String body, ContentType contentType, Credentials credentials, Header[] requestHeaders) throws URISyntaxException, HttpException {

        if (contentType != null && !StringUtils.isEmpty(body)) {
            StringEntity stringEntity = new StringEntity(body, contentType);
            stringEntity.setChunked(true);
            httpRequest.setEntity(stringEntity);
        }

        return send(httpRequest, credentials, requestHeaders);
    }

    /**
     * Performs an HTTP operation on the given URL.
     * Basic auth is used if both username and pw are not null.
     *
     * @param httpRequest    The HttpRequest to connect to.
     * @param credentials    Instance implementing {@link Credentials} interface holding a set of credentials
     * @param requestHeaders Additional HTTP headers added to the request
     * @return The HTTP response as Response object.
     * @throws URISyntaxException
     * @throws HttpException
     */
    private static Response send(HttpRequestBase httpRequest, Credentials credentials,
                                 Header[] requestHeaders) throws URISyntaxException, HttpException {

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        Response response = new Response();
        HttpClientContext httpContext = HttpClientContext.create();
        URI uri = httpRequest.getURI();

        HttpHost systemProxy = null;
        AuthScope proxyAuthScope = null;
        UsernamePasswordCredentials proxyCredentials = null;

        try {
            String uriScheme = uri.getScheme();

            String httpsProxyUser = System.getProperty("https.proxyUser");
            String httpsProxyPassword = System.getProperty("https.proxyPassword");

            String httpProxyUser = System.getProperty("http.proxyUser");
            String httpProxyPassword = System.getProperty("http.proxyPassword");

            systemProxy = getSystemProxy(uri);

            if (systemProxy != null) {
                String proxyHostName = systemProxy.getHostName();
                int proxyPort = systemProxy.getPort();
                LOG.debug("Using proxy hostname from system proxy: " + proxyHostName);
                LOG.debug("Using proxy port from system proxy: " +  proxyPort);

                proxyAuthScope = new AuthScope(systemProxy.getHostName(), systemProxy.getPort());

                if (StringUtils.equalsIgnoreCase(uriScheme, "http")) {
                    LOG.debug("Using http proxy");

                    if (!StringUtils.isEmpty(httpProxyUser) && !StringUtils.isEmpty(httpProxyPassword)) {
                        LOG.debug("Using proxy user and password for the http proxy " + proxyHostName);
                        proxyCredentials = new UsernamePasswordCredentials(httpProxyUser, httpProxyPassword);
                    }

                } else if (StringUtils.equalsIgnoreCase(uriScheme, "https")) {
                    LOG.debug("Using https proxy");

                    if (!StringUtils.isEmpty(httpsProxyUser) && !StringUtils.isEmpty(httpsProxyPassword)) {
                        LOG.debug("Using proxy user and password for the https proxy " + proxyHostName);
                        proxyCredentials = new UsernamePasswordCredentials(httpsProxyUser, httpsProxyPassword);
                    }
                }
            }
        } catch (UnknownHostException e) {
            LOG.error("Error while detecting system wide proxy: " + e.getMessage());
        }

        // set the request configuration that will passed to the httpRequest
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(httpTimeout)
            .setConnectTimeout(httpTimeout)
            .setSocketTimeout(httpTimeout)
            .setProxy(systemProxy)
            .build();

        // set (preemptive) authentication if credentials are given
        if (credentials != null || (proxyAuthScope != null && proxyCredentials != null)) {

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

            if (proxyAuthScope != null && proxyCredentials != null) {
                credentialsProvider.setCredentials(
                        proxyAuthScope,
                        proxyCredentials
                );
            }

            if (credentials != null) {
                credentialsProvider.setCredentials(
                        new AuthScope(uri.getHost(), uri.getPort()),
                        credentials
                );
            }

            HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort(),
                uri.getScheme());

            AuthCache authCache = new BasicAuthCache();
            authCache.put(targetHost, new BasicScheme());

            httpContext.setCredentialsProvider(credentialsProvider);
            httpContext.setAuthCache(authCache);

            httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        } else {

            httpClient = HttpClients.createDefault();

        }

        try {

            HttpHeaders headersMap = new HttpHeaders();

            httpRequest.setConfig(requestConfig);

            // apply HTTP header
            if (requestHeaders != null) {
                httpRequest.setHeaders(requestHeaders);
            }

            httpResponse = httpClient.execute(httpRequest, httpContext);

            HttpStatus httpStatus = HttpStatus.valueOf(
                httpResponse.getStatusLine().getStatusCode());
            Header[] headers = httpResponse.getAllHeaders();
            HttpEntity httpResponseEntity = httpResponse.getEntity();

            response.setStatusCode(httpStatus);

            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase("Transfer-Encoding") &&
                    header.getValue().equalsIgnoreCase("chunked")) {
                    LOG.trace("Removed the header 'Transfer-Encoding:chunked'" +
                        " from a response, as its handled by the http-client");
                } else {
                    headersMap.set(header.getName(), header.getValue());
                }
            }
            response.setHeaders(headersMap);

            if (httpResponseEntity != null) {
                response.setBody(EntityUtils.toByteArray(httpResponseEntity));
            }

        } catch (IOException e) {
            throw new HttpException("Error while getting a response from " + uri +
                ": " + e.getMessage());
        } finally {

            // cleanup
            httpRequest.reset();

            IOUtils.closeQuietly(httpResponse);
            IOUtils.closeQuietly(httpClient);
        }

        return response;
    }

    /**
     * Checks if request and request method are not null
     *
     * @param request {@link HttpServletRequest} to check
     * @return true if sane, false otherwise
     */
    public static boolean isSaneRequest(HttpServletRequest request) {
        if (request != null && request.getMethod() != null) {
            return true;
        }
        return false;
    }

    /**
     * Check if provided {@link HttpServletRequest} is a HTTP GET request
     *
     * @param request {@link HttpServletRequest} to check
     * @return true if HTTP v, false otherwise
     */
    public static boolean isHttpGetRequest(HttpServletRequest request) {
        boolean isSane = isSaneRequest(request);
        boolean isGet = isSane && request.getMethod().equals(HttpMethod.GET.toString());
        return isSane && isGet;
    }

    /**
     * Check if provided {@link HttpServletRequest} is a HTTP POST request
     *
     * @param request {@link HttpServletRequest} to check
     * @return true if HTTP POST, false otherwise
     */
    public static boolean isHttpPostRequest(HttpServletRequest request) {
        boolean isSane = isSaneRequest(request);
        boolean isPost = isSane && request.getMethod().equals(HttpMethod.POST.toString());
        return isSane && isPost;
    }

    /**
     * Check if provided {@link HttpServletRequest} is a FormMultipartPost
     *
     * @param request {@link HttpServletRequest} to check
     * @return true if FormMultipartPost, false otherwise
     */
    public static boolean isFormMultipartPost(HttpServletRequest request) {
        if (!isHttpPostRequest(request)) {
            return false;
        }

        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }

        if (contentType.toLowerCase().startsWith("multipart/form-data")) {
            return true;
        }

        return false;
    }

    /**
     * Extract headers from {@link HttpServletRequest}
     *
     * @param request {@link HttpServletRequest} to extract headers from
     * @return Array with {@link Header}s
     */
    public static Header[] getHeadersFromRequest(HttpServletRequest request) {
        List<Header> returnHeaderList = new ArrayList<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                // as cookies are sent in headers, we'll also have to check for unsupported cookies here
                if (headerName.toLowerCase().equals("cookie")) {
                    String[] cookies = headerValue.split(";");
                    List<String> newCookieList = new ArrayList<>();
                    for (int i = 0; i < cookies.length; i++) {
                        final String cookieFromArray = cookies[i];
                        newCookieList.add(cookieFromArray);
                    }
                    // rewrite the cookie value
                    headerValue = StringUtils.join(newCookieList, ";");
                }

                if (headerValue.isEmpty()) {
                    LOG.debug("Skipping request header '" + headerName + "' as it's value is empty (possibly an "
                        + "unsupported cookie value has been removed)");
                } else {
                    LOG.debug("Adding request header: " + headerName + "=" + headerValue);
                    returnHeaderList.add(new BasicHeader(headerName, headerValue));
                }
            }

        }

        Header[] headersArray = new Header[returnHeaderList.size()];
        headersArray = returnHeaderList.toArray(headersArray);
        return headersArray;
    }

    /**
     * Remove headers form header array that match Strings in headersToRemove
     *
     * @param headersToClean  Array of {@link Header}: Headers to clean up
     * @param headersToRemove Header names to remove
     */
    private static Header[] removeHeaders(Header[] headersToClean, String[] headersToRemove) {
        ArrayList<Header> headers = new ArrayList<>();
        if (headersToClean == null) {
            return null;
        }
        for (Header header : headersToClean) {
            if (!StringUtils.equalsAnyIgnoreCase(header.getName(), headersToRemove)) {
                headers.add(header);
            }
        }
        LOG.debug("Removed the content-length and content-type headers as the HTTP Client lib will care "
            + "about them as soon as the entity is set on the POST object.");
        Header[] headersArray = new Header[headers.size()];
        headersArray = headers.toArray(headersArray);
        return headersArray;
    }

    /**
     * Extract Request body as String of {@link HttpServletRequest}
     *
     * @param request {@link HttpServletRequest} to extract body from
     * @return
     */
    private static String getRequestBody(HttpServletRequest request) {
        String body = null;
        try (BufferedReader requestReader = request.getReader()) {
            StringBuffer bodyLines = new StringBuffer();
            String bodyLine;
            while ((bodyLine = requestReader.readLine()) != null) {
                bodyLines.append(bodyLine);
            }
            body = bodyLines.toString();
        } catch (IOException e) {
            LOG.info("Failed to obtain a Reader for a potential body of"
                + " this POST, assuming KVP");
        }

        return body;
    }

    /**
     * If the JVM knows about a HTTP proxy, e.g. by specifying
     * <p>
     * <pre>
     * 	 -Dhttp.proxyHost=schwatzgelb.de -Dhttp.proxyPort=8080
     * </pre>
     * <p>
     * as startup parameters, this method will correctly detect them and return
     * an InetSocketAddress ready to be used to pass the proxy to the
     * DefaultHttpClient.
     *
     * @param uri
     * @return
     * @throws UnknownHostException
     */
    private static HttpHost getSystemProxy(URI uri) throws UnknownHostException {

        // We do not set the java.net.useSystemProxies property here as it may
        // lead to problems in certain scenarios, e.g. when testing with OpenJDK
        // on VMs with an insufficient dependency management (see the failing
        // builds from https://github.com/terrestris/shogun-core/pull/157)

        // If it is necessary that this property is set to true, it should be
        // configured on the JVM via -Djava.net.useSystemProxies=true

        //	System.setProperty("java.net.useSystemProxies", "true");

        HttpHost systemProxy = null;

        List<Proxy> proxyList = ProxySelector.getDefault().select(uri);

        for (Iterator<Proxy> iterator = proxyList.iterator(); iterator.hasNext(); ) {
            Proxy proxy = iterator.next();
            InetSocketAddress address = (InetSocketAddress) proxy.address();

            if (address != null) {
                LOG.debug("Detected a system wide proxy: " +
                    "  * Host: " + address.getHostName() +
                    "  * Port: " + address.getPort()
                );

                systemProxy = new HttpHost(InetAddress.getByName(address.getHostName()),
                    address.getPort(), "http");
                break;

            }
        }

        return systemProxy;
    }

    /**
     * Note: The value annotation is set to the setter of httpTimeout here as
     * we can't autowire any value to its static field (but the field has to be
     * static itself).
     *
     * @param httpTimeout the httpTimeout to set
     */
    @Value("${http.timeout}")
    @SuppressWarnings("static-method")
    public void setDefaultHttpTimeout(int httpTimeout) {
        HttpUtil.defaultHttpTimeout = httpTimeout;
        HttpUtil.httpTimeout = httpTimeout;
    }

    /**
     * @return the httpTimeout
     */
    public static int getHttpTimeout() {
        return httpTimeout;
    }

    /**
     * @param httpTimeout the httpTimeout to set
     */
    public static void setHttpTimeout(int httpTimeout) {
        HttpUtil.httpTimeout = httpTimeout;
    }

    /**
     * Resets the http timeout to the default one given by the app config.
     */
    public static void resetHttpTimeout() {
        HttpUtil.httpTimeout = HttpUtil.defaultHttpTimeout;
    }
}
