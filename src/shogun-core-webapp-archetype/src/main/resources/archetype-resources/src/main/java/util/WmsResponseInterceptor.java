#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.util;

import de.terrestris.shoguncore.util.interceptor.WmsResponseInterceptorInterface;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.model.Response;

/**
 * This class demonstrates how to implement the WmsResponseInterceptorInterface.
 * 
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class WmsResponseInterceptor implements WmsResponseInterceptorInterface {

	@Override
	public Response interceptGetMap(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptGetCapabilities(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptGetFeatureInfo(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptDescribeLayer(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptGetLegendGraphic(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptGetStyles(MutableHttpServletRequest request, Response response) {
		return response;
	}

}
