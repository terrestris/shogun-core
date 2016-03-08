#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.util;

import de.terrestris.shogun2.util.interceptor.WfsResponseInterceptorInterface;
import de.terrestris.shogun2.util.model.Response;

/**
 * This class demonstrates how to implement the WfsResponseInterceptorInterface.
 * 
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class WfsResponseInterceptor implements WfsResponseInterceptorInterface{

	@Override
	public Response interceptGetCapabilities(Response response) {
		return response;
	}

	@Override
	public Response interceptDescribeFeatureType(Response response) {
		return response;
	}

	@Override
	public Response interceptGetFeature(Response response) {
		return response;
	}

	@Override
	public Response interceptLockFeature(Response response) {
		return response;
	}

	@Override
	public Response interceptTransaction(Response response) {
		return response;
	}

}
