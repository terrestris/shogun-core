package de.terrestris.shoguncore.util.interceptor;

import org.springframework.stereotype.Component;

@Component
public interface WcsRequestInterceptorInterface {

    MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptDescribeCoverage(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetCoverage(MutableHttpServletRequest request);

}
