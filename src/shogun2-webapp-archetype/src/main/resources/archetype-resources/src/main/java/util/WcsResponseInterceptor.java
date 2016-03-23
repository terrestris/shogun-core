#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.util;

import de.terrestris.shogun2.util.interceptor.WcsResponseInterceptorInterface;
import de.terrestris.shogun2.util.model.Response;

/**
 * This class demonstrates how to implement the WcsResponseInterceptorInterface.
 * 
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class WcsResponseInterceptor implements WcsResponseInterceptorInterface{

	@Override
	public Response interceptGetCapabilities(Response response) {
		return response;
	}

	@Override
	public Response interceptDescribeCoverage(Response response) {
		return response;
	}

	@Override
	public Response interceptGetCoverage(Response response) {
		return response;
	}

}
