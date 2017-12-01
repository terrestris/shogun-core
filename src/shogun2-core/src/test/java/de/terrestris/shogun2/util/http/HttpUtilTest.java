package de.terrestris.shogun2.util.http;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.terrestris.shogun2.util.model.Response;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

/**
 *
 * @author danielkoch
 * @author ahennr
 *
 */
@SuppressWarnings("static-method")
public class HttpUtilTest {

	/**
	 *
	 */
	private static final String TEST_SERVER_SCHEME = "http";

	/**
	 *
	 */
	private static final String TEST_SERVER_HOST = "127.0.0.1";

	/**
	 *
	 */
	private static final Integer TEST_SERVER_PORT = 1234;

	/**
	 *
	 */
	private static final String TEST_SERVER_INFO = "Test/1.1";

	/**
	 *
	 */
	private static final String USERNAME = "Shinji";

	/**
	 *
	 */
	private static final String PASSWORD = "Kagawa";

	/**
	 *
	 */
	private static final Credentials CREDENTIALS = new UsernamePasswordCredentials(USERNAME, PASSWORD);

	/**
	 *
	 */
	private static final List<NameValuePair> POST_KEY_VALUE_PAIRS = new ArrayList<NameValuePair>(
		Arrays.asList(
			new BasicNameValuePair("key1", "value1"),
			new BasicNameValuePair("key2", "value2"),
			new BasicNameValuePair("key3", "value3")
		)
	);

	/**
	 *
	 */
	private static final String POST_XML_BODY =
			"<root>" +
				"<element1>value1</element1>" +
				"<element2>value2</element2>" +
				"<element3>value3</element3>" +
			"</root>";

	/**
	 *
	 */
	private static final ContentType POST_XML_BODY_CONTENT_TYPE = ContentType
			.parse("application/xml");

	/**
	 *
	 */
	private static final Header[] REQ_HEADERS = {new BasicHeader("Accept-Charset", "utf-8")};

	/**
	 *
	 */
	private static URI URI;

	/**
	 *
	 */
	private static String URL;

	/**
	 *
	 */
	protected static HttpServer server;

	/**
	 *
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@BeforeClass
	public static void setUp() throws IOException, URISyntaxException {

		// simple implementation of the HttpRequestHandler
		class TestRequestHandler implements HttpRequestHandler {
			@Override
			public void handle(HttpRequest request, HttpResponse response,
					HttpContext context) throws HttpException, IOException {
				response.setEntity(new StringEntity("SHOGun2 rocks!", "UTF-8"));
			}
		};

		TestRequestHandler handler = new TestRequestHandler();

		HttpUtilTest.server = ServerBootstrap.bootstrap()
				.setLocalAddress(InetAddress.getByName(TEST_SERVER_HOST))
				.setListenerPort(TEST_SERVER_PORT)
				.setServerInfo(TEST_SERVER_INFO)
				.registerHandler("/", handler)
				.create();

		HttpUtilTest.server.start();

		URIBuilder builder = new URIBuilder();
		builder.setScheme(TEST_SERVER_SCHEME);
		builder.setHost(TEST_SERVER_HOST);
		builder.setPort(TEST_SERVER_PORT);

		HttpUtilTest.URI = builder.build();
		HttpUtilTest.URL = HttpUtilTest.URI.toString();
	}

	/**
	 *
	 */
	@AfterClass
	public static void shutDown() {
		if (HttpUtilTest.server != null) {
			HttpUtilTest.server.shutdown(10, TimeUnit.SECONDS);
		}
	}

	@Test
	public void get_url_timeout() {

	}

	@Test
	public void get_url() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URL);
		assertNotNull(response);
	}

	@Test
	public void get_url_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URL, REQ_HEADERS);
		assertNotNull(response);
	}


	@Test
	public void get_uri() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URI);
		assertNotNull(response);
	}

	@Test
	public void get_uri_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URI, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void get_url_auth_pw() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URL, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void get_url_auth_pw_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URL, USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void get_uri_auth_pw() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URI, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void get_uri_auth_pw_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URI, USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void get_url_auth_cred() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URL, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void get_url_auth_cred_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URL, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void get_uri_auth_cred() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URI, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void get_uri_auth_cred_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URI, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_empty() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL);
		assertNotNull(response);
	}

	@Test
	public void post_url_empty_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_empty() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI);
		assertNotNull(response);
	}

	@Test
	public void post_uri_empty_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_empty_auth_pw() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_url_empty_auth_pw_headers()
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_empty_auth_pw() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI,  USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_uri_empty_auth_pw_headers()
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_empty_auth_cred() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void post_url_empty_auth_cred_headers()
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_empty_auth_cred() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_empty_auth_cred_headers()
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_kvp() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_KEY_VALUE_PAIRS);
		assertNotNull(response);
	}

	@Test
	public void post_url_kvp_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_KEY_VALUE_PAIRS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_kvp_auth_pw() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_KEY_VALUE_PAIRS, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_url_kvp_auth_pw_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_KEY_VALUE_PAIRS, USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_kvp_auth_cred() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_KEY_VALUE_PAIRS, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void post_url_kvp_auth_cred_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_KEY_VALUE_PAIRS, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_kvp() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_KEY_VALUE_PAIRS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_kvp_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_KEY_VALUE_PAIRS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_kvp_auth_pw() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_KEY_VALUE_PAIRS, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_uri_kvp_auth_pw_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_KEY_VALUE_PAIRS, USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_kvp_auth_cred() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_KEY_VALUE_PAIRS, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_kvp_auth_cred_headers()
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_KEY_VALUE_PAIRS, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_body() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE);
		assertNotNull(response);
	}

	@Test
	public void post_url_body_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_body_auth_pw() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_url_body_auth_pw_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, USERNAME, PASSWORD,
				REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_body_auth_cred() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void post_url_body_auth_cred_headers()
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_body() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE);
		assertNotNull(response);
	}

	@Test
	public void post_uri_body_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_body_auth_pw() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_uri_body_auth_pw_headers() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, USERNAME, PASSWORD,
				REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_body_auth_cred() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_body_auth_cred_headers()
			throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_multipart() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URL, getTestFile());
		assertNotNull(response);
	}

	@Test
	public void post_url_multipart_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URL, getTestFile(), REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_multipart_auth_pw() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URL, getTestFile(), USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_url_multipart_auth_pw_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URL, getTestFile(), USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_url_multipart_auth_cred() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URL, getTestFile(), CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void post_url_multipart_auth_cred_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URL, getTestFile(), CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_multipart() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URI, getTestFile());
		assertNotNull(response);
	}


	@Test
	public void post_uri_multipart_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URI, getTestFile(), REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_multipart_auth_pw() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URI, getTestFile(), USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_uri_multipart_auth_pw_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URI, getTestFile(), USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_multipart_auth_cred() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URI, getTestFile(), CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_multipart_auth_cred_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.post(URI, getTestFile(), CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_uri_empty() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI);
		assertNotNull(response);
	}

	@Test
	public void put_uri_empty_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_url_empty() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL);
		assertNotNull(response);
	}

	@Test
	public void put_url_empty_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_uri_empty_auth_pw() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void put_uri_empty_auth_pw_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI, USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_url_empty_auth_pw() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL,  USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void put_url_empty_auth_pw_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL,  USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_uri_empty_auth_cred() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void put_uri_empty_auth_cred_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_url_empty_auth_cred() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void put_url_empty_auth_cred_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_uri_body() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE);
		assertNotNull(response);
	}

	@Test
	public void put_uri_body_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_uri_body_auth_pw() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void put_uri_body_auth_pw_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.put(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, USERNAME, PASSWORD,
				REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_uri_body_auth_cred() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void put_uri_body_auth_cred_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_url_body() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE);
		assertNotNull(response);
	}

	@Test
	public void put_url_body_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_url_body_auth_pw() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void put_url_body_auth_pw_headers() throws URISyntaxException, HttpException {
		Response response = HttpUtil.put(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, USERNAME, PASSWORD,
				REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void put_url_body_auth_cred() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void put_url_body_auth_cred_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.put(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	/**
	 * username and password may not be null
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void checkIllegalArgumentException_all() throws URISyntaxException, HttpException{
		HttpUtil.get(URL, null, null, null);
	}

	/**
	 * username may not be null
	 * @throws URISyntaxException
	 * @throws HttpException
	 */
	@Test(expected=IllegalArgumentException.class)
	public void checkIllegalArgumentException_user() throws URISyntaxException, HttpException{
		HttpUtil.get(URL, null, PASSWORD);
	}

	@Test
	public void delete_url() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URL);
		assertNotNull(response);
	}

	@Test
	public void delete_url_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URL, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void delete_uri() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URI);
		assertNotNull(response);
	}

	@Test
	public void delete_uri_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URI, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void delete_url_auth_pw() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URL, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void delete_url_auth_pw_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URL, USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void delete_uri_auth_pw() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URI, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void delete_uri_auth_pw_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URI, USERNAME, PASSWORD, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void delete_url_auth_cred() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URL, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void delete_url_auth_cred_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URL, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void delete_uri_auth_cred() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URI, CREDENTIALS);
		assertNotNull(response);
	}

	@Test
	public void delete_uri_auth_cred_headers() throws URISyntaxException, HttpException{
		Response response = HttpUtil.delete(URI, CREDENTIALS, REQ_HEADERS);
		assertNotNull(response);
	}

	@Test
	public void isSaneRequest_returnsFalseForNull() {
		final boolean result = HttpUtil.isSaneRequest(null);
		assertFalse("isSamerequest returns true for invalid HttpServletRequest", result);
	}

	@Test
	public void isSaneRequest_returnsTrueForDefinedMethod() {
		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.GET.toString());

		final boolean result = HttpUtil.isSaneRequest(mockedRequest);
		assertTrue("isSamerequest returns true for valid HttpServletRequest", result);
	}

	@Test
	public void isHttpGetRequest_returnsFalseForNull() {
		final boolean result = HttpUtil.isHttpGetRequest(null);
		assertFalse("isHttpGetRequest returns false for invalid HttpServletRequest", result);
	}

	@Test
	public void isHttpGetRequest_returnsFalseForIncorrectMethod() {
		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.POST.toString());

		final boolean result = HttpUtil.isHttpGetRequest(mockedRequest);
		assertFalse("isHttpGetRequest returns false for wrong method in HttpServletRequest", result);
	}

	@Test
	public void isHttpGetRequest_returnsTrueForCorrectMethod() {
		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.GET.toString());

		final boolean result = HttpUtil.isHttpGetRequest(mockedRequest);
		assertTrue("isHttpGetRequest returns true for valid HttpServletRequest", result);
	}

	@Test
	public void isHttpPostRequest_returnsFalseForNull() {
		final boolean result = HttpUtil.isHttpPostRequest(null);
		assertFalse("isHttpPostRequest returns false for invalid HttpServletRequest", result);
	}

	@Test
	public void isHttpPostRequest_returnsFalseForIncorrectMethod() {
		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.GET.toString());

		final boolean result = HttpUtil.isHttpPostRequest(mockedRequest);
		assertFalse("isHttpPostRequest returns false for wrong method in HttpServletRequest", result);
	}

	@Test
	public void isHttpPostRequest_returnsTrueForCorrectMethod() {
		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.POST.toString());

		final boolean result = HttpUtil.isHttpPostRequest(mockedRequest);
		assertTrue("isHttpPostRequest returns true for valid HttpServletRequest", result);
	}

	@Test
	public void isFormMultipartPost_returnsFalseForNull() {
		final boolean result = HttpUtil.isFormMultipartPost(null);
		assertFalse("isFormMultipartPost returns false for invalid HttpServletRequest", result);
	}

	@Test
	public void isFormMultipartPost_returnsTrueForCorrectMethod() {
		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.POST.toString());
		when(mockedRequest.getContentType()).thenReturn("multipart/form-data");

		final boolean result = HttpUtil.isFormMultipartPost(mockedRequest);
		assertTrue("isFormMultipartPost returns true for valid HttpServletRequest", result);
	}

	@Test
	public void forwardGet_notForwardHeaders() throws URISyntaxException, HttpException {
		final boolean forwardHeaders = false;

		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.GET.toString());


		final Response response = HttpUtil.forwardGet(URI, mockedRequest, forwardHeaders);
		assertNotNull(response);
	}

	@Test
	public void forwardGet_withForwardHeaders() throws URISyntaxException, HttpException {
		final boolean forwardHeaders = true;
		final String headerName = "TEST_HEADER";
		final String headerVal = "TEST_HEADER_VAL";

		final HashSet<String> headerNames = new HashSet<>();
		headerNames.add(headerName);
		final HashSet<String> headerValues = new HashSet<>();
		headerValues.add(headerVal);

		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.GET.toString());
		when(mockedRequest.getHeaderNames()).thenReturn(Collections.enumeration(headerNames));
		when(mockedRequest.getHeaders(headerName)).thenReturn(Collections.enumeration(headerValues));

		final Response response = HttpUtil.forwardGet(URI, mockedRequest, forwardHeaders);
		assertNotNull(response);
	}

	@Test
	public void forwardFormMultipartPost_withoutForwardHeaders() throws HttpException, IOException, ServletException, URISyntaxException {
		final boolean forwardHeaders = false;

		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.POST.toString());


		final Response response = HttpUtil.forwardFormMultipartPost(URI, mockedRequest, forwardHeaders);
		assertNotNull(response);
	}

	@Test
	public void forwardFormMultipartPost_withForwardHeaders() throws HttpException, IOException, ServletException, URISyntaxException {
		final boolean forwardHeaders = true;
		final String headerName = "TEST_HEADER";
		final String headerVal = "TEST_HEADER_VAL";

		final HashSet<String> headerNames = new HashSet<>();
		headerNames.add(headerName);
		headerNames.add("content-length");
		headerNames.add("content-type");
		final HashSet<String> headerValues = new HashSet<>();
		headerValues.add(headerVal);

		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.POST.toString());

		final LinkedList<Part> parts = new LinkedList<>();
		Part testPart = Mockito.mock(Part.class);
		when(testPart.getInputStream()).thenReturn(IOUtils.toInputStream("TEST_TEST", "UTF-8"));
		when(testPart.getContentType()).thenReturn("text/plain");
		when(testPart.getSubmittedFileName()).thenReturn("testFile.txt");
		when(testPart.getName()).thenReturn("file");
		parts.add(testPart);

		when(mockedRequest.getParts()).thenReturn(parts);
		when(mockedRequest.getHeaderNames()).thenReturn(Collections.enumeration(headerNames));
		when(mockedRequest.getHeaders(headerName)).thenReturn(Collections.enumeration(headerValues));
		when(mockedRequest.getHeaders("content-length")).thenReturn(Collections.enumeration(headerValues));
		when(mockedRequest.getHeaders("content-type")).thenReturn(Collections.enumeration(headerValues));

		final Response response = HttpUtil.forwardFormMultipartPost(URI, mockedRequest, forwardHeaders);
		assertNotNull(response);
	}

	@Test
	public void forwardPost_withoutForwardHeaders() throws URISyntaxException, HttpException, IOException {
		final boolean forwardHeaders = false;

		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.POST.toString());
		when(mockedRequest.getContentType()).thenReturn("text/plain");
		when(mockedRequest.getReader()).thenReturn(Mockito.mock(BufferedReader.class));

		final Response response = HttpUtil.forwardPost(URI, mockedRequest, forwardHeaders);
		assertNotNull(response);
	}

	@Test
	public void forwardPost_withForwardHeaders() throws URISyntaxException, HttpException, IOException {
		final boolean forwardHeaders = true;
		final String headerName = "TEST_HEADER";
		final String headerVal = "TEST_HEADER_VAL";

		final HashSet<String> headerNames = new HashSet<>();
		headerNames.add(headerName);
		final HashSet<String> headerValues = new HashSet<>();
		headerValues.add(headerVal);

		HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
		when(mockedRequest.getMethod()).thenReturn(HttpMethod.POST.toString());
		when(mockedRequest.getContentType()).thenReturn("text/plain");
		when(mockedRequest.getReader()).thenReturn(Mockito.mock(BufferedReader.class));
		when(mockedRequest.getHeaderNames()).thenReturn(Collections.enumeration(headerNames));
		when(mockedRequest.getHeaders(headerName)).thenReturn(Collections.enumeration(headerValues));

		final Response response = HttpUtil.forwardPost(URI, mockedRequest, forwardHeaders);
		assertNotNull(response);
	}

	private File getTestFile() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(
				"META-INF/logo.png").getFile());
		return file;
	}

}
