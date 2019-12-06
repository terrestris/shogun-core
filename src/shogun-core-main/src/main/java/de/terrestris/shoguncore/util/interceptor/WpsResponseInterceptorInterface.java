package de.terrestris.shoguncore.util.interceptor;

import de.terrestris.shoguncore.util.model.Response;
import org.springframework.stereotype.Component;

@Component
public interface WpsResponseInterceptorInterface {

    Response interceptGetCapabilities(MutableHttpServletRequest request, Response response);

    Response interceptDescribeProcess(MutableHttpServletRequest request, Response response);

    Response interceptExecute(MutableHttpServletRequest request, Response response);

}
