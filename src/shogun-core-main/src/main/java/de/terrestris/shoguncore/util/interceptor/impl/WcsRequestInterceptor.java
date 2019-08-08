/**
 *
 */
package de.terrestris.shoguncore.util.interceptor.impl;

import static org.apache.logging.log4j.LogManager.getLogger;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import de.terrestris.shoguncore.util.interceptor.GeoserverAuthHeaderRequest;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WcsRequestInterceptorInterface;

/**
 * Interceptor class for WCS requests. Adds basic auth headers based on the GS
 * properties by default.
 *
 * @author Nils Bühner
 *
 */
public class WcsRequestInterceptor implements WcsRequestInterceptorInterface {

	/**
	 *
	 */
	private static final Logger LOG = getLogger(WcsRequestInterceptor.class);

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
		LOG.debug("Intercepting WCS GetCapabilities and adding Basic auth credentials.");
		return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
	}

	/**
	 *
	 */
	@Override
	public MutableHttpServletRequest interceptDescribeCoverage(MutableHttpServletRequest request) {
		LOG.debug("Intercepting WCS DescribeCoverage and adding Basic auth credentials.");
		return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
	}

	/**
	 *
	 */
	@Override
	public MutableHttpServletRequest interceptGetCoverage(MutableHttpServletRequest request) {
		LOG.debug("Intercepting WCS GetCoverage and adding Basic auth credentials.");
		return new GeoserverAuthHeaderRequest(request, gsUser, gsPass);
	}

}
