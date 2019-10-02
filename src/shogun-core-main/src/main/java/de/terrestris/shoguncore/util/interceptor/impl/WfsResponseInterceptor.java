package de.terrestris.shoguncore.util.interceptor.impl;

import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WfsResponseInterceptorInterface;
import de.terrestris.shoguncore.util.model.Response;
import org.apache.logging.log4j.Logger;
import org.deegree.commons.xml.CommonNamespaces;
import org.deegree.commons.xml.NamespaceBindings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static javax.xml.xpath.XPathConstants.NODESET;
import static org.apache.logging.log4j.LogManager.getLogger;

public class WfsResponseInterceptor implements WfsResponseInterceptorInterface {

    private static final Logger LOG = getLogger(WfsResponseInterceptor.class);

    private void interceptGetCapabilities100(Document doc, String baseUrl) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        // xpath seems not to work with the default bindings
        NodeList list = (NodeList) xpath.compile("//*[local-name()=\"Get\"]").evaluate(doc.getDocumentElement(), NODESET);
        for (int i = 0; i < list.getLength(); ++i) {
            Element node = (Element) list.item(i);
            node.setAttribute("onlineResource", baseUrl);
        }
        list = (NodeList) xpath.compile("//*[local-name()=\"Post\"]").evaluate(doc.getDocumentElement(), NODESET);
        for (int i = 0; i < list.getLength(); ++i) {
            Element node = (Element) list.item(i);
            node.setAttribute("onlineResource", baseUrl);
        }
    }

    private void interceptGetCapabilities110And200(Document doc, String baseUrl, String owsNamespace) throws XPathExpressionException {
        NamespaceBindings nscontext = new NamespaceBindings();
        nscontext.addNamespace("ows", owsNamespace);
        nscontext.addNamespace("xlink", "http://www.w3.org/1999/xlink");
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(nscontext);
        NodeList list = (NodeList) xpath.compile("//ows:Get").evaluate(doc.getDocumentElement(), NODESET);
        for (int i = 0; i < list.getLength(); ++i) {
            Element node = (Element) list.item(i);
            node.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", baseUrl);
        }
        list = (NodeList) xpath.compile("//ows:Post").evaluate(doc.getDocumentElement(), NODESET);
        for (int i = 0; i < list.getLength(); ++i) {
            Element node = (Element) list.item(i);
            node.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", baseUrl);
        }
    }

    @Override
    public Response interceptGetCapabilities(MutableHttpServletRequest request, Response response) {
        String proto = request.getHeader("x-forwarded-proto");
        String requestHost = request.getHeader("x-forwarded-host");
        // fallbacks in case SHOGun is not behind a reverse proxy
        if (proto == null) {
            proto = "http";
        }
        if (requestHost == null) {
            requestHost = request.getHeader("host");
        }
        String baseUrl = proto + "://" + requestHost + request.getContextPath() + "/geoserver.action";
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(response.getBody()));
            Element root = doc.getDocumentElement();
            String version = root.getAttribute("version");
            switch (version) {
                case "1.0.0":
                    interceptGetCapabilities100(doc, baseUrl);
                    break;
                case "1.1.0":
                    interceptGetCapabilities110And200(doc, baseUrl, CommonNamespaces.OWS_NS);
                    break;
                case "2.0.0":
                    interceptGetCapabilities110And200(doc, baseUrl, CommonNamespaces.OWS_11_NS);
                    break;
                default:
                    throw new IOException("WFS version is not supported");
            }
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(bout));
            response.setBody(bout.toByteArray());
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException | TransformerException e) {
            LOG.warn("Problem when intercepting WFS GetCapabilities: {}", e.getMessage());
            LOG.trace("Stack trace:", e);
        }
        return response;
    }

    @Override
    public Response interceptDescribeFeatureType(MutableHttpServletRequest request, Response response) {
        return response;
    }

    @Override
    public Response interceptGetFeature(MutableHttpServletRequest request, Response response) {
        return response;
    }

    @Override
    public Response interceptLockFeature(MutableHttpServletRequest request, Response response) {
        return response;
    }

    @Override
    public Response interceptTransaction(MutableHttpServletRequest request, Response response) {
        return response;
    }

}
