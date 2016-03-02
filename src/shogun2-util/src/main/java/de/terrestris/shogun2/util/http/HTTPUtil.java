package de.terrestris.shogun2.util.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import de.terrestris.shogun2.model.Response;

/**
 *
 * @author danielkoch
 *
 */
public class HTTPUtil {

	/**
	 * The Logger.
	 */
	private static final Logger LOG = Logger.getLogger(HTTPUtil.class);

	/**
	 *
	 */
	private static final int HTTP_TIMEOUT = 30000;

	/**
	 * Performs an HTTP GET on the given URL.
	 *
	 * @param url The URL to connect to.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 */
	public static Response get(String url) throws URISyntaxException {
		return send(new HttpGet(url), null, null);
	}

	/**
	 * Performs an HTTP GET on the given URL.
	 *
	 * @param url The URL to connect to.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 */
	public static Response get(String url, String username, String password)
			throws URISyntaxException {
		return send(new HttpGet(url), username, password);
	}

	/**
	 * Performs an HTTP GET on the given URI.
	 *
	 * @param uri The URI to connect to.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 */
	public static Response get(URI uri) throws URISyntaxException {
		return send(new HttpGet(uri), null, null);
	}

	/**
	 * Performs an HTTP GET on the given URI.
	 * Basic auth is used if both username and pw are not null.
	 *
	 * @param uri The URI to connect to.
	 * @param username The Basic authentication username.
	 * @param password The Basic authentication password.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 */
	public static Response get(URI uri, String username, String password)
			throws URISyntaxException {
		return send(new HttpGet(uri), username, password);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param queryParams
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	public static Response post(String url, List<NameValuePair> queryParams)
			throws URISyntaxException, UnsupportedEncodingException {
		return postParams(new HttpPost(url), queryParams, null, null);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param queryParams
	 * @param username The Basic authentication username.
	 * @param password The Basic authentication password.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	public static Response post(String url, List<NameValuePair> queryParams,
			String username, String password) throws URISyntaxException,
			UnsupportedEncodingException {
		return postParams(new HttpPost(url), queryParams, username, password);
	}

	/**
	 * Performs an HTTP POST on the given URI.
	 *
	 * @param uri The URI to connect to.
	 * @param queryParams
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	public static Response post(URI uri, List<NameValuePair> queryParams)
			throws URISyntaxException, UnsupportedEncodingException {
		return postParams(new HttpPost(uri), queryParams, null, null);
	}

	/**
	 * Performs an HTTP POST on the given URI.
	 *
	 * @param uri The URI to connect to.
	 * @param queryParams
	 * @param username The Basic authentication username.
	 * @param password The Basic authentication password.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	public static Response post(URI uri, List<NameValuePair> queryParams,
			String username, String password) throws URISyntaxException,
			UnsupportedEncodingException {
		return postParams(new HttpPost(uri), queryParams, username, password);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param body
	 * @param contentType
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 */
	public static Response post(String url, String body, ContentType contentType)
			throws URISyntaxException {
		return postBody(new HttpPost(url), body, contentType, null, null);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param body
	 * @param contentType
	 * @param username The Basic authentication username.
	 * @param password The Basic authentication password.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 */
	public static Response post(String url, String body, ContentType contentType,
			String username, String password) throws URISyntaxException {
		return postBody(new HttpPost(url), body, contentType, username, password);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param uri The URI to connect to.
	 * @param queryParams
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	public static Response post(URI uri, String body, ContentType contentType)
			throws URISyntaxException {
		return postBody(new HttpPost(uri), body, contentType, null, null);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param uri The URI to connect to.
	 * @param body
	 * @param contentType
	 * @param username The Basic authentication username.
	 * @param password The Basic authentication password.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 */
	public static Response post(URI uri, String body, ContentType contentType,
			String username, String password) throws URISyntaxException {
		return postBody(new HttpPost(uri), body, contentType, username, password);
	}

	/**
	 *
	 * @param httpRequest
	 * @param body
	 * @param contentType
	 * @param username The Basic authentication username. No basic auth if null.
	 * @param password The Basic authentication password. No basic auth if null.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 */
	private static Response postBody(HttpPost httpRequest, String body,
			ContentType contentType, String username, String password)
			throws URISyntaxException {

		StringEntity stringEntity = new StringEntity(body, contentType);
		stringEntity.setChunked(true);
		httpRequest.setEntity(stringEntity);

		return send(httpRequest, null, null);
	}

	/**
	 *
	 * @param httpRequest
	 * @param queryParams
	 * @param username The Basic authentication username. No basic auth if null.
	 * @param password The Basic authentication password. No basic auth if null.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	private static Response postParams(HttpPost httpRequest,
			List<NameValuePair> queryParams, String username, String password)
			throws URISyntaxException, UnsupportedEncodingException {

		HttpEntity httpEntity = new UrlEncodedFormEntity(queryParams);
		httpRequest.setEntity(httpEntity);

		return send(httpRequest, username, password);
	}

	/**
	 * Performs an HTTP operation on the given URL. <BR>
	 * Basic auth is used if both username and pw are not null.
	 *
	 * @param url The URL to connect to.
	 * @param username The Basic authentication username. No basic auth if null.
	 * @param password The Basic authentication password. No basic auth if null.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 */
	private static Response send(HttpRequestBase httpRequest, String username,
			String password) throws URISyntaxException {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		Response response = new Response();
		HttpClientContext httpContext = HttpClientContext.create();
		URI uri = httpRequest.getURI();

		HttpHost systemProxy = null;
		try {
			systemProxy = getSystemProxy(uri);
		} catch (UnknownHostException e) {
			LOG.error("Error while detecting system wide proxy: " + e.getMessage());
		}

		// set the request configuration that will passed to the httpRequest
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(HTTP_TIMEOUT)
				.setConnectTimeout(HTTP_TIMEOUT)
				.setSocketTimeout(HTTP_TIMEOUT)
				.setProxy(systemProxy)
				.build();

		// set (preemptive) authentication if credentials are given
		if (username != null && password != null) {

			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(
					AuthScope.ANY,
					new UsernamePasswordCredentials(username, password)
			);

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

			httpRequest.setConfig(requestConfig);

			httpResponse = httpClient.execute(httpRequest, httpContext);
			HttpStatus httpStatus = HttpStatus.valueOf(
					httpResponse.getStatusLine().getStatusCode());
			String statusText = httpResponse.getStatusLine().getReasonPhrase();

			if (httpStatus == HttpStatus.OK) {

				response.setStatusCode(httpStatus);

				Header[] headers = httpResponse.getAllHeaders();
				HttpHeaders headersMap = new HttpHeaders();
				for (Header header : headers) {
					headersMap.set(header.getName(), header.getValue());
				}
				response.setHeaders(headersMap);

				response.setBody(EntityUtils.toByteArray(httpResponse.getEntity()));

			} else {
				LOG.info("GET-Request returned not with 200 OK: " +
						"  * URL: " + uri +
						"  * Status Code: " + httpStatus +
						"  * Reason Phrase: " + statusText);
			}
		} catch (IOException e) {
			LOG.error("Couldn't connect to " + uri + ": " + e.getMessage());
		} finally {

			// cleanup

			httpRequest.releaseConnection();

			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
				LOG.error("Could not close CloseableHttpResponse: " + e.getMessage());
			}

			try {
				httpClient.close();
			} catch (IOException e) {
				LOG.error("Could not close CloseableHttpClient: " + e.getMessage());
			}

		}

		return response;

	}

	/**
	 * If the JVM knows about a HTTP proxy, e.g. by specifying
	 *
	 * <pre>
	 *	 -Dhttp.proxyHost=schwatzgelb.de -Dhttp.proxyPort=8080
	 * </pre>
	 *
	 * as startup parameters, this method will correctly detect them and return
	 * an InetSocketAddress ready to be used to pass the proxy to the
	 * DefaultHttpClient.
	 *
	 * @param url
	 * @return
	 * @throws UnknownHostException
	 */
	private static HttpHost getSystemProxy(URI uri) throws UnknownHostException {

		System.setProperty("java.net.useSystemProxies", "true");
		HttpHost systemProxy = null;

		List<Proxy> proxyList = ProxySelector.getDefault().select(uri);

		for (Iterator<Proxy> iterator = proxyList.iterator(); iterator.hasNext();) {
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

}
