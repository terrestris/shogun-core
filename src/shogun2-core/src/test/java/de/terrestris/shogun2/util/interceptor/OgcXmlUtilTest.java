package de.terrestris.shogun2.util.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class OgcXmlUtilTest {

    public static final String XML_STRING = "<root attribute=\"value\"><element>text</element></root>";

    public static final String CORRUPTED_XML_STRING = "<root><element></not-matching-element></root>";

    @Test
    public void get_request_body_default_charset() {

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setContent(XML_STRING.getBytes());

        String body = OgcXmlUtil.getRequestBody(request);

        assertEquals(XML_STRING, body);
    }

    @Test
    public void get_request_body_utf8() {

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setContent(XML_STRING.getBytes());
        request.setCharacterEncoding("UTF-8");

        String body = OgcXmlUtil.getRequestBody(request);

        assertEquals(XML_STRING, body);
    }

    @Test
    public void get_document_from_string() throws IOException {

        Document doc = OgcXmlUtil.getDocumentFromString(XML_STRING);

        assertNotNull(doc);
    }

    @Test(expected = IOException.class)
    public void get_document_from_string_throws_exception() throws IOException {

        OgcXmlUtil.getDocumentFromString(CORRUPTED_XML_STRING);
    }

    @Test
    public void get_path_in_document_root_name() throws InterceptorException, IOException {

        Document doc = OgcXmlUtil.getDocumentFromString(XML_STRING);

        assertNotNull(doc);

        String res = OgcXmlUtil.getPathInDocument(doc, "name(/*)");

        assertEquals("root", res);
    }

    @Test
    public void get_path_in_document_attribute_value() throws InterceptorException, IOException {

        Document doc = OgcXmlUtil.getDocumentFromString(XML_STRING);

        assertNotNull(doc);

        String res = OgcXmlUtil.getPathInDocument(doc, "/*/@attribute");

        assertEquals("value", res);
    }

    @Test(expected = InterceptorException.class)
    public void get_path_in_document_throws_exception() throws InterceptorException, IOException {

        Document doc = OgcXmlUtil.getDocumentFromString(XML_STRING);

        assertNotNull(doc);

        OgcXmlUtil.getPathInDocument(doc, "invalid XPath selector");
    }

    @Test
    public void getDocumentElement() throws IOException {
        Document doc = OgcXmlUtil.getDocumentFromString(XML_STRING);
        assertNotNull(doc);

        Element element = OgcXmlUtil.getDocumentElement(doc);
        assertNotNull(element);

        assertEquals("Root name matches", element.getNodeName(), "root");
    }

}
