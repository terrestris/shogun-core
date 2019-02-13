#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.util;

import de.terrestris.shoguncore.util.interceptor.WfsResponseInterceptorInterface;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.model.Response;

/**
 * This class demonstrates how to implement the WfsResponseInterceptorInterface.
 * 
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class WfsResponseInterceptor implements WfsResponseInterceptorInterface{

	@Override
	public Response interceptGetCapabilities(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptDescribeFeatureType(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptGetFeature(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptLockFeature(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptTransaction(MutableHttpServletRequest request, Response response) {
		return response;
	}

}
