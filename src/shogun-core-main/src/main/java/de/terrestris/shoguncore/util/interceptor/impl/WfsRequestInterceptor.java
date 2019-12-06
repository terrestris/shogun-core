/**
 *
 */
package de.terrestris.shoguncore.util.interceptor.impl;

import de.terrestris.shoguncore.util.interceptor.GeoserverAuthHeaderRequest;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WfsRequestInterceptorInterface;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Interceptor class for WFS requests. Adds basic auth headers based on the GS
 * properties by default.
 *
 * @author Nils Bühner
 *
 */
public class WfsRequestInterceptor implements WfsRequestInterceptorInterface {

    /**
     *
     */
    private static final Logger LOG = getLogger(WfsRequestInterceptor.class);

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
    public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WFS GetCapabilities and adding Basic auth credentials.");
        return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptDescribeFeatureType(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WFS DescribeFeatureType and adding Basic auth credentials.");
        return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptGetFeature(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WFS GetFeature and adding Basic auth credentials.");
        return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptLockFeature(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WFS LockFeature and adding Basic auth credentials.");
        return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
    }

    /**
     *
     */
    @Override
    public MutableHttpServletRequest interceptTransaction(MutableHttpServletRequest request) {
        LOG.debug("Intercepting WFS Transaction and adding Basic auth credentials.");
        return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
    }

}
