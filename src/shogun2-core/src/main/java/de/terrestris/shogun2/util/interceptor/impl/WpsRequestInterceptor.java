/**
 *
 */
package de.terrestris.shogun2.util.interceptor.impl;

import static org.apache.logging.log4j.LogManager.getLogger;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import de.terrestris.shogun2.util.interceptor.BasicAuthHeaderRequest;
import de.terrestris.shogun2.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shogun2.util.interceptor.WpsRequestInterceptorInterface;

/**
 * Interceptor class for WPS requests. Adds basic auth headers based on the GS
 * properties by default.
 *
 * @author Nils Bühner
 *
 */
public class WpsRequestInterceptor implements WpsRequestInterceptorInterface {

	/**
	 *
	 */
	private static final Logger LOG = getLogger(WpsRequestInterceptor.class);

	/**
	 *
	 */
	@Value("${geoserver.username}")
	private String gsUser;

	/**
	 *
	 */
	@Value("${geoserver.password}")
	private String gsPass;

	/**
	 * @see de.terrestris.shogun2.util.interceptor.WpsRequestInterceptorInterface#interceptGetCapabilities(de.terrestris.shogun2.util.interceptor.MutableHttpServletRequest)
	 */
	@Override
	public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request) {
		LOG.debug("Intercepting WPS GetCapabilities and adding Basic auth credentials.");
		return new BasicAuthHeaderRequest(request, gsUser, gsPass);
	}

	/**
	 * @see de.terrestris.shogun2.util.interceptor.WpsRequestInterceptorInterface#interceptDescribeProcess(de.terrestris.shogun2.util.interceptor.MutableHttpServletRequest)
	 */
	@Override
	public MutableHttpServletRequest interceptDescribeProcess(MutableHttpServletRequest request) {
		LOG.debug("Intercepting WPS DescribeProcess and adding Basic auth credentials.");
		return new BasicAuthHeaderRequest(request, gsUser, gsPass);
	}

	/**
	 * @see de.terrestris.shogun2.util.interceptor.WpsRequestInterceptorInterface#interceptExecute(de.terrestris.shogun2.util.interceptor.MutableHttpServletRequest)
	 */
	@Override
	public MutableHttpServletRequest interceptExecute(MutableHttpServletRequest request) {
		LOG.debug("Intercepting WPS Execute and adding Basic auth credentials.");
		return new BasicAuthHeaderRequest(request, gsUser, gsPass);
	}

}
