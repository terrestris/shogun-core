package de.terrestris.shoguncore.util.interceptor;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MutableHttpServletRequestTest {

    private static final String DEFAULT_REQUEST_URI = "http://www.the-unity.de";

    private static final String DEFAULT_REQUEST_PARAMETER_KEY = "defaultKey";

    private static final String DEFAULT_REQUEST_PARAMETER_VALUE = "defaultVal";

    private static final String CUSTOM_REQUEST_URL = "http://www.schwatzgelb.de";

    private static final String CUSTOM_REQUEST_PARAMETER_KEY = "Shinji";

    private static final String CUSTOM_REQUEST_PARAMETER_VALUE = "Kagawa";

    private MutableHttpServletRequest mutableRequest;

    @Before
    public void set_up() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(DEFAULT_REQUEST_URI);
        request.addParameter(DEFAULT_REQUEST_PARAMETER_KEY,
            DEFAULT_REQUEST_PARAMETER_VALUE);
        mutableRequest = new MutableHttpServletRequest(request);
    }

    @Test
    public void wrap_request() {
        assertNotNull(mutableRequest);
    }

    @Test
    public void set_request_uri() throws URISyntaxException {
        mutableRequest.setRequestURI(new URI(CUSTOM_REQUEST_URL));
        assertEquals(CUSTOM_REQUEST_URL, mutableRequest.getRequestURI());
    }

    @Test
    public void set_request_url() {
        mutableRequest.setRequestURI(CUSTOM_REQUEST_URL);
        assertEquals(CUSTOM_REQUEST_URL, mutableRequest.getRequestURI());
    }

    @Test
    public void set_query_parameter() {
        mutableRequest.setParameter(DEFAULT_REQUEST_PARAMETER_KEY, CUSTOM_REQUEST_PARAMETER_VALUE);
        assertEquals(CUSTOM_REQUEST_PARAMETER_VALUE,
            mutableRequest.getParameter(DEFAULT_REQUEST_PARAMETER_KEY));
    }

    @Test
    public void set_query_parameter_array() {
        String[] param = new String[]{CUSTOM_REQUEST_PARAMETER_VALUE, CUSTOM_REQUEST_PARAMETER_VALUE};
        mutableRequest.setParameter(DEFAULT_REQUEST_PARAMETER_KEY, param);
        assertEquals(StringUtils.join(param, ","),
            mutableRequest.getParameter(DEFAULT_REQUEST_PARAMETER_KEY));
    }

    @Test
    public void add_query_parameter() {
        mutableRequest.addParameter(CUSTOM_REQUEST_PARAMETER_KEY, CUSTOM_REQUEST_PARAMETER_VALUE);
        assertEquals(CUSTOM_REQUEST_PARAMETER_VALUE, mutableRequest.getParameter(CUSTOM_REQUEST_PARAMETER_KEY));
    }

    @Test
    public void add_query_parameter_array() {
        String[] param = new String[]{CUSTOM_REQUEST_PARAMETER_VALUE, CUSTOM_REQUEST_PARAMETER_VALUE};
        mutableRequest.addParameter(CUSTOM_REQUEST_PARAMETER_KEY, param);
        assertEquals(StringUtils.join(param, ","),
            mutableRequest.getParameter(CUSTOM_REQUEST_PARAMETER_KEY));
    }

    @Test
    public void get_parameter_map() {
        Map<String, String[]> params = mutableRequest.getParameterMap();
        assertNotNull(params.get(DEFAULT_REQUEST_PARAMETER_KEY));
    }

    @Test
    public void get_rereadable_input_stream() throws IOException {
        ServletInputStream is = mutableRequest.getInputStream();
        assertNotNull(is);
        is.close();
        ServletInputStream is_again = mutableRequest.getInputStream();
        assertNotNull(is_again);
        is_again.close();
    }

    @Test
    public void setInputStreamFromStringBody() throws IOException {
        final String testString = "TEST_STRING";
        final String testBody = new String(testString.getBytes(), MutableHttpServletRequest.DEFAULT_CHARSET);
        mutableRequest.setInputStream(testBody);

        ServletInputStream is_again = mutableRequest.getInputStream();
        assertNotNull(is_again);
        StringWriter sw = new StringWriter();
        IOUtils.copy(is_again, sw, MutableHttpServletRequest.DEFAULT_CHARSET);
        is_again.close();

        assertEquals("String body should be equal.", sw.toString(), testString);
    }

    @Test
    public void setInputStreamFromInputStream() throws IOException {
        final String testString = "TEST_STRING";
        final InputStream testStream = new ByteArrayInputStream(testString.getBytes());
        mutableRequest.setInputStream(testStream);

        ServletInputStream is_again = mutableRequest.getInputStream();
        assertNotNull(is_again);
        StringWriter sw = new StringWriter();
        IOUtils.copy(is_again, sw, MutableHttpServletRequest.DEFAULT_CHARSET);
        is_again.close();

        assertEquals("String body should be equal.", sw.toString(), testString);
    }
}
