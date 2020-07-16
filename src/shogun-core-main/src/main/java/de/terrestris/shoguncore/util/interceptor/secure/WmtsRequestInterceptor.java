package de.terrestris.shoguncore.util.interceptor.secure;

import de.terrestris.shoguncore.dao.LayerDao;
import de.terrestris.shoguncore.model.layer.Layer;
import de.terrestris.shoguncore.model.layer.source.WmtsLayerDataSource;
import de.terrestris.shoguncore.service.LayerService;
import de.terrestris.shoguncore.util.interceptor.GeoserverAuthHeaderRequest;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WmtsRequestInterceptorInterface;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Interceptor class for WMS requests. Adds basic auth headers based on the GS
 * properties by default and checks for permission on requested layer
 *
 * @author Nils Bühner
 * @author Johannes Weskamm
 *
 */
public class WmtsRequestInterceptor extends BaseInterceptor implements WmtsRequestInterceptorInterface {

    /**
     *
     */
    private static final Logger LOG = getLogger(WmtsRequestInterceptor.class);

    /**
     *
     */
    @Autowired
    @Qualifier("layerService")
    protected LayerService<Layer, LayerDao<Layer>> layerService;

    /**
     *
     */
    @Value("${geoserver.username:}")
    private String gsUser;

    /**
     *
     */
    @Value("${geoserver.password:}")
    private String gsPass;

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetTile(MutableHttpServletRequest request, HashMap<String, Optional<String>> optionals) {
        LOG.debug("Intercepting WMTS GetTile request");

        boolean restfulRequest = optionals.containsKey("layername") &&
            optionals.get("layername").isPresent();

        if (isAllowed(
            request,
            restfulRequest ? "" : "layer",
            restfulRequest ? optionals.get("layername").get() : ""
            )) {
            String url;
            if (restfulRequest) {
                // RESTful request
                url = request.getRequestURI().split("/ows")[0] +
                    "/gwc/service/wmts/rest/" + optionals.get("layername").get() +
                    "/" + optionals.get("style").get() +
                    "/" + optionals.get("tilematrixset").get() +
                    "/" + optionals.get("tilematrix").get() +
                    "/" + optionals.get("tilerow").get() +
                    "/" + optionals.get("tilecol").get() +
                    "?" + request.getQueryString();
            } else {
                // KVP request
                url = request.getRequestURI().split("/ows")[0] +
                    "/gwc/service/wmts?" + request.getQueryString();
            }
            request.setRequestURI(url);
            return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
        } else {
            return forbidRequest(request);
        }

    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetFeatureInfo(MutableHttpServletRequest request, HashMap<String, Optional<String>> optionals) {
        LOG.debug("Intercepting WMTS GetFeatureInfo request");

        boolean restfulRequest = optionals.containsKey("layername") &&
            optionals.get("layername").isPresent();

        if (isAllowed(
            request,
            restfulRequest ? "" : "layer",
            restfulRequest ? optionals.get("layername").get() : ""
            )) {
            String url;
            if (restfulRequest) {
                // RESTful request
                url = request.getRequestURI().split("/ows")[0] +
                    "/gwc/service/wmts/rest/" + optionals.get("layername").get() +
                    "/" + optionals.get("style").get() +
                    "/" + optionals.get("tilematrixset").get() +
                    "/" + optionals.get("tilematrix").get() +
                    "/" + optionals.get("tilerow").get() +
                    "/" + optionals.get("tilecol").get() +
                    "/" + optionals.get("j").get() +
                    "/" + optionals.get("i").get() +
                    "?" + request.getQueryString();
            } else {
                // KVP request
                url = request.getRequestURI().split("/ows")[0] +
                    "/gwc/service/wmts?" + request.getQueryString();
            }
            request.setRequestURI(url);
            return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
        } else {
            return forbidRequest(request);
        }
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMTS GetCapabilities request");
        // response will be intercepted
        String url = request.getRequestURI().split("/ows")[0] +
            "/gwc/service/wmts?REQUEST=GetCapabilities";
        request.setRequestURI(url);
        return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
    }

    /**
     * Check for read permission on layer by getting it through service
     *
     * @param request The request
     * @param paramName The optional parameter name for the layer parameter
     * @return if the layer is allowed to read for current user
     */
    private boolean isAllowed(MutableHttpServletRequest request,
        String paramName, String layerName) {
        String layer;
        if (!StringUtils.isEmpty(paramName)) {
            layer = request.getParameterIgnoreCase(paramName);
        } else {
            layer = layerName;
        }
        List<Layer> all = layerService.findAll();
        boolean match = false;
        for (Layer l : all) {
            if (l.getSource() instanceof WmtsLayerDataSource) {
                WmtsLayerDataSource source = (WmtsLayerDataSource)
                    l.getSource();
                String sourceLayer = source.getWmtsLayer();
                if (sourceLayer.contains(":") && !layer.contains(":")) {
                    sourceLayer = sourceLayer.split(":")[1];
                } else if (layer.contains(":") && !sourceLayer.contains(":")) {
                    layer = layer.split(":")[1];
                }
                if (sourceLayer.equalsIgnoreCase(layer)) {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }

}
