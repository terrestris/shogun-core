package de.terrestris.shogun2.util.interceptor;

import org.springframework.stereotype.Component;

import de.terrestris.shogun2.util.model.Response;

@Component
public interface WfsResponseInterceptorInterface {

	public Response interceptGetCapabilities(MutableHttpServletRequest request, Response response);

	public Response interceptDescribeFeatureType(MutableHttpServletRequest request, Response response);

	public Response interceptGetFeature(MutableHttpServletRequest request, Response response);

	public Response interceptLockFeature(MutableHttpServletRequest request, Response response);

	public Response interceptTransaction(MutableHttpServletRequest request, Response response);

}
