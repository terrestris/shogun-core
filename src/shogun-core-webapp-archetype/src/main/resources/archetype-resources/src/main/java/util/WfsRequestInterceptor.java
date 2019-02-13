#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.util;

import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WfsRequestInterceptorInterface;

/**
 * This class demonstrates how to implement the WfsRequestInterceptorInterface.
 * 
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class WfsRequestInterceptor implements WfsRequestInterceptorInterface {

	@Override
	public MutableHttpServletRequest interceptGetCapabilities(
			MutableHttpServletRequest request) {
		return request;
	}

	@Override
	public MutableHttpServletRequest interceptDescribeFeatureType(
			MutableHttpServletRequest request) {
		return request;
	}

	@Override
	public MutableHttpServletRequest interceptGetFeature(
			MutableHttpServletRequest request) {
		return request;
	}

	@Override
	public MutableHttpServletRequest interceptLockFeature(
			MutableHttpServletRequest request) {
		return request;
	}

	@Override
	public MutableHttpServletRequest interceptTransaction(
			MutableHttpServletRequest request) {
		return request;
	}

}
