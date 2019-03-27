package de.terrestris.shogun2.util.interceptor.standard;

import de.terrestris.shogun2.dao.LayerDataSourceDao;
import de.terrestris.shogun2.model.layer.source.ImageWmsLayerDataSource;
import de.terrestris.shogun2.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shogun2.util.interceptor.WmsResponseInterceptorInterface;
import de.terrestris.shogun2.util.model.Response;
import org.apache.logging.log4j.Logger;
import org.deegree.commons.xml.CommonNamespaces;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
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
import java.util.stream.Collectors;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * This class demonstrates how to implement the WmsResponseInterceptorInterface.
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class WmsResponseInterceptor implements WmsResponseInterceptorInterface {

    private static final Logger LOG = getLogger(WmsResponseInterceptor.class);

    @Autowired
    @Qualifier("layerDataSourceDao")
    private LayerDataSourceDao<ImageWmsLayerDataSource> layerDataSourceDao;

    @Override
    public Response interceptGetMap(MutableHttpServletRequest request, Response response) {
        // the existing http header
        HttpHeaders existingHeaders = response.getHeaders();
        // the new http header
        HttpHeaders modifiedHeaders = new HttpHeaders();

        // make a copy of all existing http headers
        modifiedHeaders.putAll(existingHeaders);

        // add no-cache headers
        modifiedHeaders.setCacheControl("no-cache, no-store, must-revalidate");
        modifiedHeaders.setPragma("no-cache");
        modifiedHeaders.setExpires(0);

        response.setHeaders(modifiedHeaders);

        return response;
    }

    private void removeLayers(Document doc, String namespace, List<String> layerNames) throws XPathExpressionException {
        layerNames = layerNames.parallelStream().map(layerName -> layerName.split(":")[1]).collect(Collectors.toList());
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        NamespaceContext nscontext = CommonNamespaces.getNamespaceContext();
        xpath.setNamespaceContext(nscontext);
        String prefix = namespace.equals("") ? "" : "wms:";
        XPathExpression expr = xpath.compile("//" + prefix + "Layer/" + prefix + "Name");
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        List<Element> toRemove = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Element name = (Element) nodeList.item(i);
            String str = name.getTextContent();
            if (!layerNames.contains(str)) {
                toRemove.add((Element) name.getParentNode());
            }
        }
        toRemove.forEach(element -> element.getParentNode().removeChild(element));
    }

    @Override
    @Transactional(value = Transactional.TxType.REQUIRED)
    public Response interceptGetCapabilities(MutableHttpServletRequest request, Response response) {
        String endpoint = request.getParameterIgnoreCase("CUSTOM_ENDPOINT");
        if (endpoint == null) {
            return response;
        }
        LogicalExpression where = Restrictions.and(
            Restrictions.eq("requestableByPath", true),
            Restrictions.eq("customRequestPath", endpoint)
        );
        List<ImageWmsLayerDataSource> sources = this.layerDataSourceDao.findByCriteria(where);
        List<String> layerNames = sources.parallelStream().map(ImageWmsLayerDataSource::getLayerNames).collect(Collectors.toList());

        byte[] body = response.getBody();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(body));
            Element root = doc.getDocumentElement();
            String version = root.getAttribute("version");
            System.out.println(version);
            if (version == null) {
                return response;
            }
            if (version.equals("1.3.0")) {
                removeLayers(doc, "http://www.opengis.net/wms", layerNames);
            } else {
                removeLayers(doc, "", layerNames);
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);
            transformer.transform(source, result);
            response.setBody(bos.toByteArray());
        } catch (Throwable e) {
            LOG.warn("Something went wrong when intercepting a get capabilities response: " + e.getMessage());
            LOG.trace("Stack trace", e);
        }
        return response;
    }

    @Override
    public Response interceptGetFeatureInfo(MutableHttpServletRequest request, Response response) {
        return response;
    }

    @Override
    public Response interceptDescribeLayer(MutableHttpServletRequest request, Response response) {
        return response;
    }

    @Override
    public Response interceptGetLegendGraphic(MutableHttpServletRequest request, Response response) {
        return response;
    }

    @Override
    public Response interceptGetStyles(MutableHttpServletRequest request, Response response) {
        return response;
    }

}
