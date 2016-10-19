package de.terrestris.shogun2.util.http;

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
import java.util.Iterator;
import java.util.List;

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
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import de.terrestris.shogun2.util.model.Response;

/**
 *
 * @author Daniel Koch
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Component
public class HttpUtil {

	/**
	 * The Logger.
	 */
	private static final Logger LOG = Logger.getLogger(HttpUtil.class);

	/**
	 * The timeout for all outgoing HTTP connections.
	 */
	private static int httpTimeout;

	/**
	 * Performs an HTTP GET on the given URL <i>without authentication</i>
	 *
	 * @param url The URL to connect to.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response get(String url) throws URISyntaxException, HttpException {
		return send(new HttpGet(url), null);
	}

	/**
	 * Performs an HTTP GET on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param credentials instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response get(String url, Credentials credentials)
			throws URISyntaxException, HttpException {
		return send(new HttpGet(url), credentials);
	}

	/**
	 * Performs an HTTP GET on the given URL
	 *
	 * @param url The URL to connect to.
	 * @param username Credentials - username
	 * @param password Credentials - password
	 * @throws HttpException
	 * @throws URISyntaxException
	 */
	public static Response get(String url, String username, String password) throws URISyntaxException, HttpException {
		return send(new HttpGet(url), new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP GET on the given URI.
	 * No credentials needed.
	 *
	 * @param uri The URI to connect to.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response get(URI uri) throws URISyntaxException, HttpException {
		return send(new HttpGet(uri), null);
	}

	/**
	 * Performs an HTTP GET on the given URI.
	 * Basic auth is used if both username and password are not null.
	 *
	 * @param uri The URI to connect to.
	 * @param username Credentials - username
	 * @param password Credentials - password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response get(URI uri, String username, String password)
			throws URISyntaxException, HttpException {
		return send(new HttpGet(uri), new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP GET on the given URI.
	 * Basic auth is used if both username and pw are not null.
	 *
	 * @param uri The URI to connect to.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response get(URI uri, Credentials credentials)
			throws URISyntaxException, HttpException {
		return send(new HttpGet(uri), credentials);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param url The URL to connect to.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(String url)
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(url), new ArrayList<NameValuePair>(), null);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 * Basic auth is used if both and password are not null.
	 *
	 * @param uri The URI to connect to.
	 * @param username Credentials - username
	 * @param password Credentials - password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(String url, String password, String username)
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(url), new ArrayList<NameValuePair>(), new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 * Basic auth is used if credentials object is not null
	 *
	 * @param url The URL to connect to.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(String url, Credentials credentials)
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(url), new ArrayList<NameValuePair>(), credentials);
	}

	/**
	 * Performs an HTTP POST on the given URI.
	 *
	 * @param uri The URI to connect to.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(URI uri)
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(uri), new ArrayList<NameValuePair>(), null);
	}

	/**
	 * Performs an HTTP POST on the given URI.
	 * Basic auth is used if both and password are not null.
	 *
	 * @param uri The URI to connect to.
	 * @param username Credentials - username
	 * @param password Credentials - password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(URI uri, String username, String password)
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(uri), new ArrayList<NameValuePair>(), new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP POST on the given URI.
	 * Basic auth is used if credentials object is not null
	 *
	 * @param uri The URI to connect to.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(URI uri, Credentials credentials)
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(uri), new ArrayList<NameValuePair>(), credentials);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param queryParams The list of NameValuePairs.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(String url, List<NameValuePair> queryParams)
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(url), queryParams, null);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 * Basic auth is used if both and password are not null.
	 *
	 * @param url The URL to connect to.
	 * @param queryParams The list of NameValuePairs.
	 * @param username Credentials - username
	 * @param password Credentials - password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(String url, List<NameValuePair> queryParams, String username, String password) throws URISyntaxException,
			UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(url), queryParams, new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param queryParams The list of NameValuePairs.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(String url, List<NameValuePair> queryParams, Credentials credentials) throws URISyntaxException,
			UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(url), queryParams, credentials);
	}

	/**
	 * Performs an HTTP POST on the given URI.
	 *
	 * @param uri The URI to connect to.
	 * @param queryParams The list of NameValuePairs.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(URI uri, List<NameValuePair> queryParams)
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(uri), queryParams, null);
	}

	/**
	 * Performs an HTTP POST on the given URI.
	 * Basic auth is used if both and password are not null.
	 *
	 * @param uri The URI to connect to.
	 * @param queryParams The list of NameValuePairs.
	 * @param username Credentials - username
	 * @param password Credentials - password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(URI uri, List<NameValuePair> queryParams, String username, String password) throws URISyntaxException,
			UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(uri), queryParams, new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP POST on the given URI.
	 *
	 * @param uri The URI to connect to.
	 * @param queryParams The list of NameValuePairs.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	public static Response post(URI uri, List<NameValuePair> queryParams, Credentials credentials) throws URISyntaxException,
			UnsupportedEncodingException, HttpException {
		return postParams(new HttpPost(uri), queryParams, credentials);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param body The POST body.
	 * @param contentType The ContentType of the POST body.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(String url, String body, ContentType contentType)
			throws URISyntaxException, HttpException {
		return postBody(new HttpPost(url), body, contentType, null);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 * Basic auth is used is both username and password are not null
	 *
	 * @param url The URL to connect to.
	 * @param body The POST body.
	 * @param contentType The ContentType of the POST body
	 * @param username Credentials - username
	 * @param password Credentials - password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(String url, String body, ContentType contentType, String username, String password) throws URISyntaxException, HttpException {
		return postBody(new HttpPost(url), body, contentType, new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param body The POST body.
	 * @param contentType The ContentType of the POST body.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(String url, String body, ContentType contentType,
			Credentials credentials) throws URISyntaxException, HttpException {
		return postBody(new HttpPost(url), body, contentType, credentials);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param uri The URI to connect to.
	 * @param body The POST body.
	 * @param contentType The ContentType of the POST body.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 * @throws UnsupportedEncodingException
	 */
	public static Response post(URI uri, String body, ContentType contentType)
			throws URISyntaxException, HttpException {
		return postBody(new HttpPost(uri), body, contentType, null);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 * Basic auth is used if both username and password are not null
	 *
	 * @param uri The URI to connect to.
	 * @param body The POST body.
	 * @param contentType The ContentType of the POST body.
	 * @param username Credentials - username
	 * @param password Credentials - password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(URI uri, String body, ContentType contentType,
			String username, String password) throws URISyntaxException, HttpException {
		return postBody(new HttpPost(uri), body, contentType, new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param uri The URI to connect to.
	 * @param body The POST body.
	 * @param contentType The ContentType of the POST body.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(URI uri, String body, ContentType contentType,
			Credentials credentials) throws URISyntaxException, HttpException {
		return postBody(new HttpPost(uri), body, contentType, credentials);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param file The file to send as MultiPartFile.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(String url, File file) throws URISyntaxException, HttpException {
		return postMultiPart(new HttpPost(url), new FileBody(file), null);
	}

	/**
	 *
	 * Performs an HTTP POST on the given URL.
	 * Basic auth is used if both username and password are not null
	 *
	 * @param url The URL to connect to.
	 * @param file The file to send as MultiPartFile.
	 * @param username username
	 * @param password password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(String url, File file, String username, String password) throws URISyntaxException, HttpException {
		return postMultiPart(new HttpPost(url), new FileBody(file), new UsernamePasswordCredentials(username, password));
	}

	/**
	 *
	 * Performs an HTTP POST on the given URL.
	 * Basic auth is used if credentials object is not null.
	 *
	 * @param url The URL to connect to.
	 * @param file The file to send as MultiPartFile.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(String url, File file, Credentials credentials) throws URISyntaxException, HttpException {
		return postMultiPart(new HttpPost(url), new FileBody(file), credentials);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 *
	 * @param uri The URI to connect to.
	 * @param file The file to send as MultiPartFile.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(URI uri, File file) throws URISyntaxException, HttpException {
		return postMultiPart(new HttpPost(uri), new FileBody(file), null);
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 * Basic auth is used if both username and password are not null.
	 *
	 * @param uri The URI to connect to.
	 * @param file The file to send as MultiPartFile.
	 * @param username username
	 * @param password password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(URI uri, File file, String username, String password) throws URISyntaxException, HttpException {
		return postMultiPart(new HttpPost(uri), new FileBody(file), new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP POST on the given URL.
	 * Basic auth is used if credentials object is not null.
	 *
	 * @param uri The URI to connect to.
	 * @param file The file to send as MultiPartFile.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response post(URI uri, File file, Credentials credentials) throws URISyntaxException, HttpException {
		return postMultiPart(new HttpPost(uri), new FileBody(file), credentials);
	}

	/**
	 *
	 * @param httpRequest
	 * @param file
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	private static Response postMultiPart(HttpPost httpRequest, FileBody file, Credentials credentials) throws URISyntaxException, HttpException {

		HttpEntity multiPartEntity = MultipartEntityBuilder.create()
				.addPart("file", file)
				.build();
		httpRequest.setEntity(multiPartEntity);

		return send(httpRequest, credentials);
	}

	/**
	 *
	 * @param httpRequest
	 * @param body
	 * @param contentType
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	private static Response postBody(HttpPost httpRequest, String body, ContentType contentType, Credentials credentials)
			throws URISyntaxException, HttpException {

		StringEntity stringEntity = new StringEntity(body, contentType);
		stringEntity.setChunked(true);
		httpRequest.setEntity(stringEntity);

		return send(httpRequest, credentials);
	}

	/**
	 *
	 * @param httpRequest
	 * @param queryParams
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws HttpException
	 */
	private static Response postParams(HttpPost httpRequest, List<NameValuePair> queryParams, Credentials credentials)
			throws URISyntaxException, UnsupportedEncodingException, HttpException {

		HttpEntity httpEntity = new UrlEncodedFormEntity(queryParams);
		httpRequest.setEntity(httpEntity);

		return send(httpRequest, credentials);
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
		return putBody(new HttpPut(uri), null, null, null);
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
		return putBody(new HttpPut(uriString), null, null, null);
	}

	/**
	 * Perform HTTP PUT with empty body
	 * Basic auth will be used if both username and password are not null
	 *
	 * @param uri URI to connect to
	 * @param username username
	 * @param password password
	 *
	 * @return
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response put(URI uri, String username, String password) throws URISyntaxException, HttpException {
		return putBody(new HttpPut(uri), null, null, new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Perform HTTP PUT with empty body
	 *
	 * @param uri URI to connect to
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response put(URI uri, Credentials credentials) throws URISyntaxException, HttpException {
		return putBody(new HttpPut(uri), null, null, credentials);
	}

	/**
	 * Perform HTTP PUT with empty body
	 *
	 * @param uriString String representing the URI to connect to
	 * @param username username
	 * @param password password
	 *
	 * @return
	 * @throws HttpException
	 * @throws URISyntaxException
	 */
	public static Response put(String uriString, String username, String password) throws URISyntaxException, HttpException {
		return putBody(new HttpPut(uriString), null, null, new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Perform HTTP PUT with empty body
	 *
	 * @param uriString String representing the URI to connect to
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return
	 * @throws HttpException
	 * @throws URISyntaxException
	 */
	public static Response put(String uriString, Credentials credentials) throws URISyntaxException, HttpException {
		return putBody(new HttpPut(uriString), null, null, credentials);
	}

	/**
	 * Performs an HTTP PUT on the given URL.
	 * (without BasicAuth)
	 *
	 * @param uriString String representing the URI to connect to
	 * @param body
	 * @param contentType
	 *
	 * @return
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response put(String uriString, String body, ContentType contentType) throws URISyntaxException, HttpException{
		return putBody(new HttpPut(uriString), body, contentType, null);
	}

	/**
	 * Performs an HTTP PUT on the given URL.
	 *
	 * @param uriString String representing the URI to connect to
	 * @param body
	 * @param contentType
	 * @param username
	 * @param password
	 *
	 * @return
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response put(String uriString, String body, ContentType contentType, String username, String password) throws URISyntaxException, HttpException{
		return putBody(new HttpPut(uriString), body, contentType, new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP PUT on the given URL.
	 *
	 * @param uriString
	 * @param body
	 * @param contentType
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response put(String uriString, String body, ContentType contentType, Credentials credentials) throws URISyntaxException, HttpException{
		return putBody(new HttpPut(uriString), body, contentType, credentials);
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
	public static Response put(URI uri, String body, ContentType contentType) throws URISyntaxException, HttpException{
		return putBody(new HttpPut(uri), body, contentType, null);
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
	public static Response put(URI uri, String body, ContentType contentType, String username, String password) throws URISyntaxException, HttpException{
		return putBody(new HttpPut(uri), body, contentType, new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP PUT on the given URL.
	 *
	 * @param uri
	 * @param body
	 * @param contentType
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response put(URI uri, String body, ContentType contentType, Credentials credentials) throws URISyntaxException, HttpException{
		return putBody(new HttpPut(uri), body, contentType, credentials);
	}

	/**
	 * Performs an HTTP DELETE on the given URL.
	 *
	 * @param url The URL to connect to.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response delete(String url) throws URISyntaxException, HttpException {
		return send(new HttpDelete(url), null);
	}

	/**
	 * Performs an HTTP DELETE on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param username username
	 * @param password password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response delete(String url, String username, String password)
			throws URISyntaxException, HttpException {
		return send(new HttpDelete(url), new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP DELETE on the given URL.
	 *
	 * @param url The URL to connect to.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response delete(String url, Credentials credentials)
			throws URISyntaxException, HttpException {
		return send(new HttpDelete(url), credentials);
	}

	/**
	 * Performs an HTTP DELETE on the given URI.
	 *
	 * @param uri The URI to connect to.
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response delete(URI uri) throws URISyntaxException, HttpException {
		return send(new HttpDelete(uri), null);
	}

	/**
	 * Performs an HTTP DELETE on the given URI.
	 * Basic auth is used if both username and pw are not null.
	 *
	 * @param uri The URI to connect to.
	 * @param username username
	 * @param password password
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response delete(URI uri, String username, String password) throws URISyntaxException, HttpException {
		return send(new HttpDelete(uri), new UsernamePasswordCredentials(username, password));
	}

	/**
	 * Performs an HTTP DELETE on the given URI.
	 * Basic auth is used if both username and pw are not null.
	 *
	 * @param uri The URI to connect to.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	public static Response delete(URI uri, Credentials credentials)
			throws URISyntaxException, HttpException {
		return send(new HttpDelete(uri), credentials);
	}

	/**
	 *
	 * @param httpRequest
	 * @param body
	 * @param contentType
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	private static Response putBody(HttpPut httpRequest, String body, ContentType contentType, Credentials credentials) throws URISyntaxException, HttpException {

		if (contentType != null && !StringUtils.isEmpty(body)) {
			StringEntity stringEntity = new StringEntity(body, contentType);
			stringEntity.setChunked(true);
			httpRequest.setEntity(stringEntity);
		}

		return send(httpRequest, credentials);
	}

	/**
	 * Performs an HTTP operation on the given URL.
	 * Basic auth is used if both username and pw are not null.
	 *
	 * @param httpRequest The HttpRequest to connect to.
	 * @param credentials Instance implementing {@link Credentials} interface holding a set of credentials
	 *
	 * @return The HTTP response as Response object.
	 *
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	private static Response send(HttpRequestBase httpRequest, Credentials credentials) throws URISyntaxException, HttpException {

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
				.setConnectionRequestTimeout(httpTimeout)
				.setConnectTimeout(httpTimeout)
				.setSocketTimeout(httpTimeout)
				.setProxy(systemProxy)
				.build();

		// set (preemptive) authentication if credentials are given
		if (credentials != null) {

			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(
					AuthScope.ANY,
					credentials
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

			HttpHeaders headersMap = new HttpHeaders();

			httpRequest.setConfig(requestConfig);

			httpResponse = httpClient.execute(httpRequest, httpContext);

			HttpStatus httpStatus = HttpStatus.valueOf(
					httpResponse.getStatusLine().getStatusCode());
			Header[] headers = httpResponse.getAllHeaders();
			HttpEntity httpResponseEntity = httpResponse.getEntity();

			response.setStatusCode(httpStatus);

			for (Header header : headers) {

				if (header.getName().equalsIgnoreCase("Transfer-Encoding") &&
					header.getValue().equalsIgnoreCase("chunked")) {
					LOG.debug("Removed the header 'Transfer-Encoding:chunked'" +
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

		// We do not set the java.net.useSystemProxies property here as it may
		// lead to problems in certain scenarios, e.g. when testing with OpenJDK
		// on VMs with an insufficient dependency management (see the failing
		// builds from https://github.com/terrestris/shogun2/pull/157)

		// If it is necessary that this property is set to true, it should be
		// configured on the JVM via -Djava.net.useSystemProxies=true

		//	System.setProperty("java.net.useSystemProxies", "true");

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

	/**
	 * Note: The value annotation is set to the setter of httpTimeout here as
	 * we can't autowire any value to its static field (but the field has to be
	 * static itself).
	 *
	 * @param httpTimeout the httpTimeout to set
	 */
	@Value("${http.timeout}")
	@SuppressWarnings("static-method")
	public void setHttpTimeout(int httpTimeout) {
		HttpUtil.httpTimeout = httpTimeout;
	}

}
