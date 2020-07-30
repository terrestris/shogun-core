package de.terrestris.shoguncore.util.interceptor;

import org.springframework.stereotype.Component;

@Component
public interface WmtsRequestInterceptorInterface {

    MutableHttpServletRequest interceptGetTile(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetFeatureInfo(MutableHttpServletRequest request);

}
