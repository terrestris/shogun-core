package de.terrestris.shoguncore.util.interceptor;

import org.springframework.stereotype.Component;

@Component
public interface WfsRequestInterceptorInterface {

    MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptDescribeFeatureType(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetFeature(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptLockFeature(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptTransaction(MutableHttpServletRequest request);

}
