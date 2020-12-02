package de.terrestris.shoguncore.util.interceptor.secure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.terrestris.shoguncore.dao.LayerDao;
import de.terrestris.shoguncore.model.layer.Layer;
import de.terrestris.shoguncore.model.layer.source.ImageWmsLayerDataSource;
import de.terrestris.shoguncore.model.layer.source.TileWmsLayerDataSource;
import de.terrestris.shoguncore.model.layer.source.WfsLayerDataSource;
import de.terrestris.shoguncore.service.LayerService;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WfsResponseInterceptorInterface;
import de.terrestris.shoguncore.util.model.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.deegree.commons.xml.CommonNamespaces;
import org.deegree.commons.xml.NamespaceBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static javax.xml.xpath.XPathConstants.NODESET;
import static org.apache.logging.log4j.LogManager.getLogger;

public class WfsResponseInterceptor implements WfsResponseInterceptorInterface {

    private static final Logger LOG = getLogger(WfsResponseInterceptor.class);

    /**
     *
     */
    @Autowired
    @Qualifier("layerService")
    protected LayerService<Layer, LayerDao<Layer>> layerService;

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
        LOG.debug("Intercepting WFS GetCapabilities response");
        String endpoint = request.getParameterIgnoreCase("CUSTOM_ENDPOINT");
        if (endpoint == null) {
            return null;
        }
        String proto = request.getHeader("x-forwarded-proto");
        String requestHost = request.getHeader("x-forwarded-host");
        // fallbacks in case SHOGun is not behind a reverse proxy
        if (StringUtils.isEmpty(proto)) {
            proto = request.getScheme();
        }
        if (StringUtils.isEmpty(requestHost)) {
            requestHost = request.getHeader("x-forwarded-for");
        }
        if (StringUtils.isEmpty(requestHost)) {
            requestHost = request.getServerName();
        }
        String baseUrl = proto + "://" + requestHost + request.getContextPath() + "/geoserver.action/" + endpoint;
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
            removeLayers(doc);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(bout));
            response.setBody(bout.toByteArray());
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException | TransformerException e) {
            LOG.warn("Problem when intercepting WFS GetCapabilities: {}", e.getMessage());
            LOG.trace("Stack trace:", e);
            return null;
        }
        return response;
    }

    private void removeLayers(Document doc) throws XPathExpressionException {
        // get all layers allowed for this user in order to filter out not allowed ones
        List<Layer> layers = layerService.findAll();
        List<String> layerNames = new ArrayList<>();
        for (Layer layer : layers) {
            if (layer.getSource() instanceof ImageWmsLayerDataSource) {
                ImageWmsLayerDataSource source = (ImageWmsLayerDataSource) layer.getSource();
                layerNames.add(source.getLayerNames());
            } else if (layer.getSource() instanceof WfsLayerDataSource) {
                WfsLayerDataSource source = (WfsLayerDataSource) layer.getSource();
                layerNames.add(source.getTypeName());
                layerNames.add(source.getTypeNames());
            }
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(CommonNamespaces.getNamespaceContext().
            addNamespace("wfs", "http://www.opengis.net/wfs"));
        NodeList list = (NodeList) xpath.compile("//wfs:FeatureType/wfs:Name").evaluate(doc.getDocumentElement(), NODESET);
        List<Element> toRemove = new ArrayList<>();
        determineFeatureTypeNodesToRemove(layerNames, list, toRemove);
        xpath.setNamespaceContext(CommonNamespaces.getNamespaceContext().
            addNamespace("wfs", "http://www.opengis.net/wfs/2.0"));
        list = (NodeList) xpath.compile("//wfs:FeatureType/wfs:Name").evaluate(doc.getDocumentElement(), NODESET);
        determineFeatureTypeNodesToRemove(layerNames, list, toRemove);
        toRemove.forEach(element -> element.getParentNode().removeChild(element));
    }

    private void determineFeatureTypeNodesToRemove(List<String> layerNames, NodeList list, List<Element> toRemove) {
        for (int i = 0; i < list.getLength(); ++i) {
            Element name = (Element) list.item(i);
            String str = name.getTextContent();
            if (!layerNames.contains(str)) {
                toRemove.add((Element) name.getParentNode());
            }
        }
    }

    @Override
    public Response interceptDescribeFeatureType(MutableHttpServletRequest request, Response response) {
        LOG.debug("Intercepting WFS DescribeFeatureType response");
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            if (response.getHeaders().get("content-type").get(0).toLowerCase(Locale.ROOT).contains("json")) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonObject = objectMapper.readTree(response.getBody());
                String ns = jsonObject.get("targetPrefix").asText();
                ArrayNode featureTypes = (ArrayNode) jsonObject.get("featureTypes");
                List<Layer> allowedLayers = layerService.findAll();
                int size = featureTypes.size();
                for (int i = 0; i < size; i++) {
                    String typeName = ns + ":" + featureTypes.get(i).get("typeName").asText();
                    boolean match = false;
                    for (Layer allowedLayer : allowedLayers) {
                        if (allowedLayer.getSource() instanceof TileWmsLayerDataSource) {
                            String names = ((TileWmsLayerDataSource) allowedLayer.getSource()).getLayerNames();
                            if (names.equalsIgnoreCase(typeName)) {
                                match = true;
                                break;
                            }
                        }
                    }
                    if (!match) {
                        featureTypes.remove(i);
                    }
                }
                final byte[] bytes = objectMapper.writeValueAsBytes(jsonObject);
                response.setBody(bytes);
                return response;
            }
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(response.getBody()));
            removeFeatureTypes(doc);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(bout));
            response.setBody(bout.toByteArray());
        } catch (NullPointerException | ParserConfigurationException | SAXException | IOException | XPathExpressionException | TransformerException e) {
            LOG.warn("Problem when intercepting WFS GetCapabilities: {}", e.getMessage());
            LOG.trace("Stack trace:", e);
            return null;
        }
        return response;
    }

    private void removeFeatureTypes(Document doc) throws XPathExpressionException {
        // get all layers allowed for this user in order to filter out not allowed ones
        List<Layer> layers = layerService.findAll();
        List<String> layerNames = new ArrayList<>();
        for (Layer layer : layers) {
            if (layer.getSource() instanceof ImageWmsLayerDataSource) {
                ImageWmsLayerDataSource source = (ImageWmsLayerDataSource) layer.getSource();
                if (source.getLayerNames().contains(":")) {
                    layerNames.add(source.getLayerNames().split(":")[1]);
                } else {
                    layerNames.add(source.getLayerNames());
                }
            } else if (layer.getSource() instanceof WfsLayerDataSource) {
                WfsLayerDataSource source = (WfsLayerDataSource) layer.getSource();
                if (source.getTypeName().contains(":")) {
                    layerNames.add(source.getTypeName().split(":")[1]);
                } else {
                    layerNames.add(source.getTypeName());
                }
                if (source.getTypeNames().contains(":")) {
                    layerNames.add(source.getTypeNames().split(":")[1]);
                } else {
                    layerNames.add(source.getTypeNames());
                }
            }
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(CommonNamespaces.getNamespaceContext().
            addNamespace("wfs", "http://www.opengis.net/wfs").
            addNamespace("xsd", "http://www.w3.org/2001/XMLSchema"));
        NodeList list = (NodeList) xpath.compile("//xsd:complexType").evaluate(doc.getDocumentElement(), NODESET);
        List<Element> toRemove = new ArrayList<>();
        for (int i = 0; i < list.getLength(); ++i) {
            Element name = (Element) list.item(i);
            String str = name.getAttribute("name");
            str = str.substring(0, str.lastIndexOf("Type"));
            if (!layerNames.contains(str)) {
                toRemove.add(name);
            }
        }
        list = (NodeList) xpath.compile("/xsd:schema/xsd:element").evaluate(doc.getDocumentElement(), NODESET);
        for (int i = 0; i < list.getLength(); ++i) {
            Element name = (Element) list.item(i);
            String str = name.getAttribute("name");
            if (!layerNames.contains(str)) {
                toRemove.add(name);
            }
        }
        toRemove.forEach(element -> element.getParentNode().removeChild(element));
    }

    @Override
    public Response interceptGetFeature(MutableHttpServletRequest request, Response response) {
        LOG.debug("Intercepting WFS GetFeature response");
        return response;
    }

    @Override
    public Response interceptLockFeature(MutableHttpServletRequest request, Response response) {
        LOG.debug("Intercepting WFS LockFeature response");
        return response;
    }

    @Override
    public Response interceptTransaction(MutableHttpServletRequest request, Response response) {
        LOG.debug("Intercepting WFS Transaction response");
        return response;
    }

}
