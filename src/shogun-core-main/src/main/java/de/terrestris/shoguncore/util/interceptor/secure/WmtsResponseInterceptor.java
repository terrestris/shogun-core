package de.terrestris.shoguncore.util.interceptor.secure;

import de.terrestris.shoguncore.dao.LayerDao;
import de.terrestris.shoguncore.model.layer.Layer;
import de.terrestris.shoguncore.model.layer.source.WmtsLayerDataSource;
import de.terrestris.shoguncore.service.LayerService;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WmtsResponseInterceptorInterface;
import de.terrestris.shoguncore.util.model.Response;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.deegree.commons.xml.CommonNamespaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.transaction.Transactional;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author Daniel Koch
 * @author Andreas Schmitz
 * @author Johannes Weskamm
 * @author terrestris GmbH & Co. KG
 */
public class WmtsResponseInterceptor implements WmtsResponseInterceptorInterface {

    private static final Logger LOG = getLogger(WmsResponseInterceptor.class);

    /**
     *
     */
    @Autowired
    @Qualifier("layerService")
    protected LayerService<Layer, LayerDao<Layer>> layerService;

    /**
     * Intercepts the response in order to filter the capabilities document.
     * URLs will be replaced to match the geoserver.action interface and
     * layers without permission will be removed from the document
     *
     * @param request The request
     * @param response The response to intercept
     */
    @Override
    @Transactional(value = Transactional.TxType.REQUIRED)
    public Response interceptGetCapabilities(MutableHttpServletRequest request, Response response) {
        String endpoint = request.getParameterIgnoreCase("CUSTOM_ENDPOINT");
        if (endpoint == null) {
            return null;
        }

        // get all layers allowed for this user in order to filter out not allowed ones
        List<Layer> layers = layerService.findAll();
        List<String> layerNames = new ArrayList<String>();
        for (Layer layer : layers) {
            if (layer.getSource() instanceof WmtsLayerDataSource) {
                WmtsLayerDataSource source = (WmtsLayerDataSource) layer.getSource();
                layerNames.add(source.getWmtsLayer());
            }
        }

        byte[] body = response.getBody();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(body));
            String proto = request.getHeader("x-forwarded-proto");
            String host = request.getHeader("x-forwarded-host");
            if (StringUtils.isEmpty(proto)) {
                proto = request.getScheme();
            }
            if (StringUtils.isEmpty(host)) {
                host = request.getServerName();
            }
            String baseUrl = proto + "://" + host + request.getParameter("CONTEXT_PATH") + "/geoserver.action/" + endpoint;
            removeLayers(doc, "wmts", layerNames);
            updateUrls(doc, "wmts", baseUrl);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);
            transformer.transform(source, result);
            response.setBody(bos.toByteArray());
        } catch (Throwable e) {
            LOG.warn("Something went wrong when intercepting a get capabilities response: " + e.getMessage());
            LOG.trace("Stack trace", e);
            return null;
        }
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response interceptGetTile(MutableHttpServletRequest mutableRequest, Response response) {
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response interceptGetFeatureInfo(MutableHttpServletRequest mutableRequest, Response response) {
        return response;
    }

    /**
     * Removes layers from the capabilities document
     *
     * @param doc The document
     * @param namespace The namespace
     * @param layerNames The layerNames to keep
     * @throws XPathExpressionException
     */
    @SuppressFBWarnings("UC_USELESS_OBJECT")
    private void removeLayers(Document doc, String namespace, List<String> layerNames) throws XPathExpressionException {
        List<String> unqualifiedLayerNames = new ArrayList<String>();
        for (String name : layerNames) {
            if (name.contains(":")) {
                unqualifiedLayerNames.add(name.split(":")[1]);
            } else {
                unqualifiedLayerNames.add(name);
            }
        }
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        String owsVersion = doc.getDocumentElement().getAttribute("xmlns:ows");
        String wmtsVersion = doc.getDocumentElement().getAttribute("xmlns");
        NamespaceContext nscontext = CommonNamespaces.getNamespaceContext().
            addNamespace("ows", owsVersion).
            addNamespace("wmts", wmtsVersion);
        xpath.setNamespaceContext(nscontext);
        String prefix = namespace.equals("") ? "" : "wmts:";
        XPathExpression expr = xpath.compile("//" + prefix + "Layer/ows:Identifier");
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        List<Element> toRemove = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Element name = (Element) nodeList.item(i);
            String str = name.getTextContent();
            if (!unqualifiedLayerNames.contains(str)) {
                toRemove.add((Element) name.getParentNode());
            } else {
                String nodeLayerName = name.getTextContent();
                for (int j = 0; j < layerNames.size(); j++) {
                    String qualifiedLayerName = layerNames.get(j);
                    if (qualifiedLayerName.contains(":")) {
                        String layerNamespace = qualifiedLayerName.split(":")[0];
                        String unqualifiedLayerName = qualifiedLayerName.split(":")[1];
                        if (nodeLayerName.equals(unqualifiedLayerName)) {
                            name.setTextContent(layerNamespace + ":" + name.getTextContent());
                        }
                    } else {
                        if (nodeLayerName.equals(qualifiedLayerName)) {
                            name.setTextContent(name.getTextContent());
                        }
                    }
                }
            }
        }
        toRemove.forEach(element -> element.getParentNode().removeChild(element));
    }

    /**
     * Rewrites URLs in the capabilites document
     *
     * @param doc The document
     * @param namespace The namespace
     * @param baseUrl The baseUrl
     * @throws XPathExpressionException
     */
    private void updateUrls(Document doc, String namespace, String baseUrl) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        String owsVersion = doc.getDocumentElement().getAttribute("xmlns:ows");
        String wmtsVersion = doc.getDocumentElement().getAttribute("xmlns");
        NamespaceContext nscontext = CommonNamespaces.getNamespaceContext().
            addNamespace("ows", owsVersion).
            addNamespace("wmts", wmtsVersion);
        xpath.setNamespaceContext(nscontext);
        XPathExpression expr = xpath.compile("//@xlink:href");
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); ++i) {
            String url = nodeList.item(i).getNodeValue();
            int index = url.indexOf("?");
            if (index > -1) {
                url = url.substring(index);
                url = baseUrl + url;
                nodeList.item(i).setNodeValue(url);
            } else {
                nodeList.item(i).setNodeValue(baseUrl);
            }
        }
        XPathExpression expr2 = xpath.compile("//wmts:Layer//@template");
        NodeList nodeList2 = (NodeList) expr2.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodeList2.getLength(); ++i) {
            String url = nodeList2.item(i).getNodeValue();
            int index = url.indexOf("gwc/service/wmts/rest");
            if (index > -1) {
                String path = url.split("gwc/service/wmts/rest")[1];
                url = baseUrl + path;
                nodeList2.item(i).setNodeValue(url);
            } else {
                nodeList2.item(i).setNodeValue(baseUrl);
            }
        }
    }
}
