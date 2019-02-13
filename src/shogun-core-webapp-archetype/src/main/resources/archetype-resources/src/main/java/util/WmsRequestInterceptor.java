#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.util;

import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.interceptor.WmsRequestInterceptorInterface;

/**
 * This class demonstrates how to implement the WmsRequestInterceptorInterface.
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class WmsRequestInterceptor implements WmsRequestInterceptorInterface {

	@Override
	public MutableHttpServletRequest interceptGetMap(
			MutableHttpServletRequest request) {
		// Manipulate request as you like…
		return request;
	}

	@Override
	public MutableHttpServletRequest interceptGetCapabilities(
			MutableHttpServletRequest request) {
		return request;
	}

	@Override
	public MutableHttpServletRequest interceptGetFeatureInfo(
			MutableHttpServletRequest request) {
		return request;
	}

	@Override
	public MutableHttpServletRequest interceptDescribeLayer(
			MutableHttpServletRequest request) {
		return request;
	}

	@Override
	public MutableHttpServletRequest interceptGetLegendGraphic(
			MutableHttpServletRequest request) {
		return request;
	}

	@Override
	public MutableHttpServletRequest interceptGetStyles(
			MutableHttpServletRequest request) {
		return request;
	}

}
