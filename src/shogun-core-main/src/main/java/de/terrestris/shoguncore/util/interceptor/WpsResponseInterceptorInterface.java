package de.terrestris.shoguncore.util.interceptor;

import org.springframework.stereotype.Component;

import de.terrestris.shoguncore.util.model.Response;

@Component
public interface WpsResponseInterceptorInterface {

    public Response interceptGetCapabilities(MutableHttpServletRequest request, Response response);

    public Response interceptDescribeProcess(MutableHttpServletRequest request, Response response);

    public Response interceptExecute(MutableHttpServletRequest request, Response response);

}
