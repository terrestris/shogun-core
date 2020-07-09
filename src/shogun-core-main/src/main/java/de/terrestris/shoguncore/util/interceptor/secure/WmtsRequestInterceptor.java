package de.terrestris.shoguncore.util.interceptor.secure;

import de.terrestris.shoguncore.dao.LayerDao;
import de.terrestris.shoguncore.model.layer.Layer;
import de.terrestris.shoguncore.model.layer.source.WmtsLayerDataSource;
import de.terrestris.shoguncore.service.LayerService;
import de.terrestris.shoguncore.util.application.ShogunCoreContextUtil;
import de.terrestris.shoguncore.util.interceptor.GeoserverAuthHeaderRequest;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WmtsRequestInterceptorInterface;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Interceptor class for WMS requests. Adds basic auth headers based on the GS
 * properties by default and checks for permission on requested layer
 *
 * @author Nils Bühner
 * @author Johannes Weskamm
 *
 */
public class WmtsRequestInterceptor implements WmtsRequestInterceptorInterface {

    /**
     *
     */
    private URI appUri = null;

    /**
     *
     */
    private static final Logger LOG = getLogger(WmsRequestInterceptor.class);

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
    public MutableHttpServletRequest interceptGetTile(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMTS GetTile request");
        return isAllowed(request) ? new GeoserverAuthHeaderRequest(
            request, gsUser, gsPass) : forbidRequest(request);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetFeatureInfo(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMTS GetFeatureInfo request");
        return isAllowed(request) ? new GeoserverAuthHeaderRequest(
            request, gsUser, gsPass) : forbidRequest(request);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMTS GetCapabilities request");
        // response will be intercepted
        String url = request.getRequestURI().split("/ows") [0] +
            "/gwc/service/wmts" + request.getQueryString();
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
        String paramName) {
        String layersParam = request.getParameterIgnoreCase(paramName);
        List<Layer> all = layerService.findAll();
        boolean match = false;
        for (Layer layer : all) {
            if (layer.getSource() instanceof WmtsLayerDataSource) {
                WmtsLayerDataSource source = (WmtsLayerDataSource)
                    layer.getSource();
                if (source.getWmtsLayer().equalsIgnoreCase(layersParam)) {
                    match = true;
                }
            }
        }
        return match;
    }

    /**
     * Calls main method with default "layers" parameter
     *
     * @param request The request
     * @return If the layer is allowed to read for current user
     */
    private boolean isAllowed(MutableHttpServletRequest request) {
        return isAllowed(request, "layer");
    }

    /**
     * <p>forbidRequest.</p>
     *
     * @param request a {@link de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest} object.
     * @return a {@link de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest} object.
     */
    protected MutableHttpServletRequest forbidRequest(MutableHttpServletRequest request) {
        this.setAppUriFromRequest(request);
        String redirectUri = appUri == null ? "" : appUri.toString();
        request.setRequestURI(redirectUri + "/response/forbidden.action");
        return request;
    }

    /**
     * <p>setAppUriFromRequest.</p>
     *
     * @param request a {@link de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest} object.
     */
    protected void setAppUriFromRequest(MutableHttpServletRequest request) {
        if (appUri == null) {
            try {
                appUri = ShogunCoreContextUtil.getApplicationURIFromRequest(request);
            } catch (URISyntaxException e) {
                // pass
            }
        }
    }

}
