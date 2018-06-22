package de.terrestris.shogun2.util.interceptor;

import org.springframework.stereotype.Component;

@Component
public interface WpsRequestInterceptorInterface {

    public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    public MutableHttpServletRequest interceptDescribeProcess(MutableHttpServletRequest request);

    public MutableHttpServletRequest interceptExecute(MutableHttpServletRequest request);

}
