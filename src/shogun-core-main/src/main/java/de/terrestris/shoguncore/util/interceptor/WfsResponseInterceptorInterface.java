package de.terrestris.shoguncore.util.interceptor;

import de.terrestris.shoguncore.util.model.Response;
import org.springframework.stereotype.Component;

@Component
public interface WfsResponseInterceptorInterface {

    Response interceptGetCapabilities(MutableHttpServletRequest request, Response response);

    Response interceptDescribeFeatureType(MutableHttpServletRequest request, Response response);

    Response interceptGetFeature(MutableHttpServletRequest request, Response response);

    Response interceptLockFeature(MutableHttpServletRequest request, Response response);

    Response interceptTransaction(MutableHttpServletRequest request, Response response);

}
