package de.terrestris.shogun2.util.interceptor;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class OgcXmlUtil {

    /**
     * The Logger.
     */
    private static final Logger LOG = Logger.getLogger(OgcXmlUtil.class);

    /**
     * The default charset.
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * @param request
     * @return
     */
    public static String getRequestBody(HttpServletRequest request) {

        ServletInputStream in = null;
        String body = null;

        try {
            in = request.getInputStream();

            String encoding = request.getCharacterEncoding();
            Charset charset;
            if (!StringUtils.isEmpty(encoding)) {
                charset = Charset.forName(encoding);
            } else {
                charset = Charset.forName(DEFAULT_CHARSET);
            }
            body = StreamUtils.copyToString(in, charset);
        } catch (IOException e) {
            LOG.error("Could not read the InputStream as String: " +
                e.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
        }

        return body;

    }

    /**
     * @param xml
     * @return
     * @throws IOException
     */
    public static Document getDocumentFromString(String xml) throws IOException {

        Document document = null;

        try {
            InputSource source = new InputSource(new StringReader(xml));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(source);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IOException("Could not parse input body " +
                "as XML: " + e.getMessage());
        }

        return document;

    }

    /**
     * @param document
     * @param path
     * @return
     * @throws InterceptorException
     */
    public static String getPathInDocument(Document document, String path)
        throws InterceptorException {

        if (document == null) {
            throw new InterceptorException("Document may not be null");
        }

        if (StringUtils.isEmpty(path)) {
            throw new InterceptorException("Missing parameter path");
        }

        String result;

        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(path);
            result = expr.evaluate(document, XPathConstants.STRING).toString();
        } catch (XPathExpressionException e) {
            throw new InterceptorException("Error while selecting document " +
                "element with XPath: " + e.getMessage());
        }

        return result;
    }

    /**
     * @param document
     * @param path
     * @return
     * @throws InterceptorException
     */
    public static NodeList getPathInDocumentAsNodeList(Document document, String path)
        throws InterceptorException {

        if (document == null) {
            throw new InterceptorException("Document may not be null");
        }

        if (StringUtils.isEmpty(path)) {
            throw new InterceptorException("Missing parameter path");
        }

        NodeList result;

        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(path);
            result = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new InterceptorException("Error while selecting document " +
                "element with XPath: " + e.getMessage());
        }

        return result;
    }

    /**
     * Return the document {@link Element} of a {@link Document}
     *
     * @param doc The {@link Document} to obtain the document element from
     * @return
     */
    public static Element getDocumentElement(Document doc) {
        Element docElement = null;
        if (doc != null) {
            docElement = doc.getDocumentElement();
            if (docElement != null) {
                // optional, but recommended
                // see here: http://bit.ly/1h2Ybzb
                docElement.normalize();
            }
        }
        return docElement;
    }

}
