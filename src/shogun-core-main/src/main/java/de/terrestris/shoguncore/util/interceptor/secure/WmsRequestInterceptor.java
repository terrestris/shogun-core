package de.terrestris.shoguncore.util.interceptor.secure;

import de.terrestris.shoguncore.dao.LayerDao;
import de.terrestris.shoguncore.model.layer.Layer;
import de.terrestris.shoguncore.model.layer.source.ImageWmsLayerDataSource;
import de.terrestris.shoguncore.service.LayerService;
import de.terrestris.shoguncore.util.interceptor.GeoserverAuthHeaderRequest;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WmsRequestInterceptorInterface;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import static org.apache.logging.log4j.LogManager.getLogger;
import java.util.List;

/**
 * Interceptor class for WMS requests. Adds basic auth headers based on the GS
 * properties by default and checks for permission on requested layer
 *
 * @author Nils Bühner
 * @author Johannes Weskamm
 *
 */
public class WmsRequestInterceptor extends BaseInterceptor implements WmsRequestInterceptorInterface {

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
    public MutableHttpServletRequest interceptGetMap(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMS GetMap request");
        return isAllowed(request) ? new GeoserverAuthHeaderRequest(
            request, gsUser, gsPass) : forbidRequest(request);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetFeatureInfo(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMS GetFeatureInfo request");
        return isAllowed(request) ? new GeoserverAuthHeaderRequest(
            request, gsUser, gsPass) : forbidRequest(request);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptDescribeLayer(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMS DescribeLayer request");
        return isAllowed(request) ? new GeoserverAuthHeaderRequest(
            request, gsUser, gsPass) : forbidRequest(request);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetLegendGraphic(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMS GetLegendGraphic request");
        return isAllowed(request, "layer") ? new GeoserverAuthHeaderRequest(
            request, gsUser, gsPass) : forbidRequest(request);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetStyles(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMS GetStyles request");
        return isAllowed(request) ? new GeoserverAuthHeaderRequest(
            request, gsUser, gsPass) : forbidRequest(request);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WMS GetCapabilities request");
        // response will be intercepted
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
            if (layer.getSource() instanceof ImageWmsLayerDataSource) {
                ImageWmsLayerDataSource source = (ImageWmsLayerDataSource)
                    layer.getSource();
                if (source.getLayerNames().equalsIgnoreCase(layersParam) &&
                    source.getUrl().equalsIgnoreCase(request.getContextPath() +
                    "/geoserver.action")) {
                    match = true;
                    break;
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
        return isAllowed(request, "layers");
    }

}
