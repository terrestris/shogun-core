package de.terrestris.shogun2.util.interceptor;

import org.springframework.stereotype.Component;

import de.terrestris.shogun2.util.model.Response;

@Component
public interface WfsResponseInterceptorInterface {

	public Response interceptGetCapabilities(Response response);

	public Response interceptDescribeFeatureType(Response response);

	public Response interceptGetFeature(Response response);

	public Response interceptLockFeature(Response response);

	public Response interceptTransaction(Response response);

}
