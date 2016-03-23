#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.util;

import de.terrestris.shogun2.util.interceptor.WmsResponseInterceptorInterface;
import de.terrestris.shogun2.util.model.Response;

/**
 * This class demonstrates how to implement the WmsResponseInterceptorInterface.
 * 
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class WmsResponseInterceptor implements WmsResponseInterceptorInterface {

	@Override
	public Response interceptGetMap(Response response) {
		return response;
	}

	@Override
	public Response interceptGetCapabilities(Response response) {
		return response;
	}

	@Override
	public Response interceptGetFeatureInfo(Response response) {
		return response;
	}

	@Override
	public Response interceptDescribeLayer(Response response) {
		return response;
	}

	@Override
	public Response interceptGetLegendGraphic(Response response) {
		return response;
	}

	@Override
	public Response interceptGetStyles(Response response) {
		return response;
	}

}
