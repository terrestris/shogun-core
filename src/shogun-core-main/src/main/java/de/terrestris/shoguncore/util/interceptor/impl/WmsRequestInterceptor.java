/**
 *
 */
package de.terrestris.shoguncore.util.interceptor.impl;

import static org.apache.logging.log4j.LogManager.getLogger;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import de.terrestris.shoguncore.util.interceptor.GeoserverAuthHeaderRequest;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WmsRequestInterceptorInterface;

/**
 * Interceptor class for WMS requests. Adds basic auth headers based on the GS
 * properties by default.
 *
 * @author Nils Bühner
 *
 */
public class WmsRequestInterceptor implements WmsRequestInterceptorInterface {

	/**
	 *
	 */
	private static final Logger LOG = getLogger(WmsRequestInterceptor.class);

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
		LOG.debug("Intercepting WMS GetMap and adding Basic auth credentials.");
		return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
	}

	/**
	 *
	 */
	@Override
	public MutableHttpServletRequest interceptGetFeatureInfo(MutableHttpServletRequest request) {
		LOG.debug("Intercepting WMS GetFeatureInfo and adding Basic auth credentials.");
		return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
	}

	/**
	 *
	 */
	@Override
	public MutableHttpServletRequest interceptDescribeLayer(MutableHttpServletRequest request) {
		LOG.debug("Intercepting WMS DescribeLayer and adding Basic auth credentials.");
		return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
	}

	/**
	 *
	 */
	@Override
	public MutableHttpServletRequest interceptGetLegendGraphic(MutableHttpServletRequest request) {
		LOG.debug("Intercepting WMS GetLegendGraphic and adding Basic auth credentials.");
		return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
	}

	/**
	 *
	 */
	@Override
	public MutableHttpServletRequest interceptGetStyles(MutableHttpServletRequest request) {
		LOG.debug("Intercepting WMS GetStyles and adding Basic auth credentials.");
		return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
	}

	/**
	 *
	 */
	@Override
	public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request) {
		LOG.debug("Intercepting WMS GetCapabilities and adding Basic auth credentials.");
		return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
	}

}
