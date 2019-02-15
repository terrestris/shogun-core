package de.terrestris.shoguncore.util.interceptor;

import org.springframework.stereotype.Component;

@Component
public interface WcsRequestInterceptorInterface {

    public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    public MutableHttpServletRequest interceptDescribeCoverage(MutableHttpServletRequest request);

    public MutableHttpServletRequest interceptGetCoverage(MutableHttpServletRequest request);

}
