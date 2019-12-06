package de.terrestris.shoguncore.util.interceptor;

import de.terrestris.shoguncore.util.model.Response;
import org.springframework.stereotype.Component;

@Component
public interface WmsResponseInterceptorInterface {

    Response interceptGetMap(MutableHttpServletRequest request, Response response);

    Response interceptGetCapabilities(MutableHttpServletRequest request, Response response);

    Response interceptGetFeatureInfo(MutableHttpServletRequest request, Response response);

    Response interceptDescribeLayer(MutableHttpServletRequest request, Response response);

    Response interceptGetLegendGraphic(MutableHttpServletRequest request, Response response);

    Response interceptGetStyles(MutableHttpServletRequest request, Response response);

}
