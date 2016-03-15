package de.terrestris.shogun2.util.http;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.message.BasicNameValuePair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.terrestris.shogun2.util.model.Response;

/**
 *
 * @author danielkoch
 *
 */
@SuppressWarnings("static-method")
public class HttpUtilTest {

	/**
	 * The class to test.
	 */
	@Autowired
	private HttpUtil httpUtil;

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

		HttpUtilTest.server = ServerBootstrap.bootstrap()
				.setLocalAddress(InetAddress.getByName(TEST_SERVER_HOST))
				.setListenerPort(TEST_SERVER_PORT)
				.setServerInfo(TEST_SERVER_INFO)
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
	public void get_uri() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URI);
		assertNotNull(response);
	}

	@Test
	public void get_url_auth() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URL, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void get_uri_auth() throws URISyntaxException, HttpException {
		Response response = HttpUtil.get(URI, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_url_kvp() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_KEY_VALUE_PAIRS);
		assertNotNull(response);
	}

	@Test
	public void post_url_kvp_auth() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_KEY_VALUE_PAIRS, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_uri_kvp() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_KEY_VALUE_PAIRS);
		assertNotNull(response);
	}

	@Test
	public void post_uri_kvp_auth() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_KEY_VALUE_PAIRS, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_url_body() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE);
		assertNotNull(response);
	}

	@Test
	public void post_url_body_auth() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URL, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, USERNAME, PASSWORD);
		assertNotNull(response);
	}

	@Test
	public void post_uri_body() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE);
		assertNotNull(response);
	}

	@Test
	public void post_uri_body_auth() throws URISyntaxException, UnsupportedEncodingException, HttpException {
		Response response = HttpUtil.post(URI, POST_XML_BODY, POST_XML_BODY_CONTENT_TYPE, USERNAME, PASSWORD);
		assertNotNull(response);
	}

}
