#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.util;

import de.terrestris.shoguncore.util.interceptor.WcsResponseInterceptorInterface;
import de.terrestris.shoguncore.util.interceptor.MutableHttpServletRequest;
import de.terrestris.shoguncore.util.model.Response;

/**
 * This class demonstrates how to implement the WcsResponseInterceptorInterface.
 * 
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class WcsResponseInterceptor implements WcsResponseInterceptorInterface{

	@Override
	public Response interceptGetCapabilities(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptDescribeCoverage(MutableHttpServletRequest request, Response response) {
		return response;
	}

	@Override
	public Response interceptGetCoverage(MutableHttpServletRequest request, Response response) {
		return response;
	}

}
