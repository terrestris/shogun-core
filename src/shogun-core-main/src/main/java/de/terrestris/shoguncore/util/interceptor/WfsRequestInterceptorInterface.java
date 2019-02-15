package de.terrestris.shoguncore.util.interceptor;

import org.springframework.stereotype.Component;

@Component
public interface WfsRequestInterceptorInterface {

    public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    public MutableHttpServletRequest interceptDescribeFeatureType(MutableHttpServletRequest request);

    public MutableHttpServletRequest interceptGetFeature(MutableHttpServletRequest request);

    public MutableHttpServletRequest interceptLockFeature(MutableHttpServletRequest request);

    public MutableHttpServletRequest interceptTransaction(MutableHttpServletRequest request);

}
