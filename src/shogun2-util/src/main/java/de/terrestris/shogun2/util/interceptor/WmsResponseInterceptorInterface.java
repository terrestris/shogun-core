package de.terrestris.shogun2.util.interceptor;

import org.springframework.stereotype.Component;

import de.terrestris.shogun2.util.model.Response;

@Component
public interface WmsResponseInterceptorInterface {

	public Response interceptGetMap(Response response);

	public Response interceptGetCapabilities(Response response);

	public Response interceptGetFeatureInfo(Response response);

	public Response interceptDescribeLayer(Response response);

	public Response interceptGetLegendGraphic(Response response);

	public Response interceptGetStyles(Response response);

}
