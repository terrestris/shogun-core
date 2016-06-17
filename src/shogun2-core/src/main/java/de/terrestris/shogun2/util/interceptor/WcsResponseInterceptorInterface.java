package de.terrestris.shogun2.util.interceptor;

import org.springframework.stereotype.Component;

import de.terrestris.shogun2.util.model.Response;

@Component
public interface WcsResponseInterceptorInterface {

	public Response interceptGetCapabilities(MutableHttpServletRequest request, Response response);

	public Response interceptDescribeCoverage(MutableHttpServletRequest request, Response response);

	public Response interceptGetCoverage(MutableHttpServletRequest request, Response response);

}
