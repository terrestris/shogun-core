package de.terrestris.shoguncore.util.interceptor;

import org.springframework.stereotype.Component;

@Component
public interface WpsRequestInterceptorInterface {

    MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptDescribeProcess(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptExecute(MutableHttpServletRequest request);

}
