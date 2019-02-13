package de.terrestris.shoguncore.util.interceptor;

import org.springframework.stereotype.Component;

import de.terrestris.shoguncore.util.model.Response;

@Component
public interface WmsResponseInterceptorInterface {

    public Response interceptGetMap(MutableHttpServletRequest request, Response response);

    public Response interceptGetCapabilities(MutableHttpServletRequest request, Response response);

    public Response interceptGetFeatureInfo(MutableHttpServletRequest request, Response response);

    public Response interceptDescribeLayer(MutableHttpServletRequest request, Response response);

    public Response interceptGetLegendGraphic(MutableHttpServletRequest request, Response response);

    public Response interceptGetStyles(MutableHttpServletRequest request, Response response);

}
