package de.terrestris.shoguncore.util.interceptor.secure;

import de.terrestris.shoguncore.dao.LayerDao;
import de.terrestris.shoguncore.model.layer.Layer;
import de.terrestris.shoguncore.service.LayerService;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WmtsRequestInterceptorInterface;
import de.terrestris.shoguncore.util.interceptor.WmtsUtil;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.apache.logging.log4j.LogManager.getLogger;

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
    @Override
    public MutableHttpServletRequest interceptGetTile(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMTS GetTile request");
        Layer layer = isAllowed(request);
        if (layer != null) {
            String url = layer.getSource().getUrl();
            String layerId = WmtsUtil.getLayerId(request);
            url += request.getRequestURL().toString().split("/wmts/"  + layerId)[1];
            request.setRequestURI(url);
        } else {
            return forbidRequest(request);
        }
        return request;
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetFeatureInfo(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMTS GetFeatureInfo request");
        Layer layer = isAllowed(request);
        if (layer != null) {
            String url = layer.getSource().getUrl();
            String layerId = WmtsUtil.getLayerId(request);
            url += request.getRequestURL().toString().split("/wmts/"  + layerId)[1];
            request.setRequestURI(url);
        } else {
            return forbidRequest(request);
        }
        return request;
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMTS GetCapabilities request");
        // response will be intercepted, we reroute to GeoServers WMTS endpoint
        String url = request.getRequestURI().split("/ows")[0] +
            "/gwc/service/wmts?REQUEST=GetCapabilities";
        request.setRequestURI(url);
        return request;
    }

    /**
     * Check for read permission on layer by getting it through service
     *
     * @param request The request
     * @return if the layer is allowed to read for current user
     */
    private Layer isAllowed(MutableHttpServletRequest request) {
        return layerService.findById(Integer.parseInt(WmtsUtil.getLayerId(request)));
    }

}
