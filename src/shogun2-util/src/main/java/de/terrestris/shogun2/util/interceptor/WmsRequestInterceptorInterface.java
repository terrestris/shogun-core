package de.terrestris.shogun2.util.interceptor;

import org.springframework.stereotype.Component;

@Component
public interface WmsRequestInterceptorInterface {

	public MutableHttpServletRequest interceptGetMap(MutableHttpServletRequest request);

	public MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

	public MutableHttpServletRequest interceptGetFeatureInfo(MutableHttpServletRequest request);

	public MutableHttpServletRequest interceptDescribeLayer(MutableHttpServletRequest request);

	public MutableHttpServletRequest interceptGetLegendGraphic(MutableHttpServletRequest request);

	public MutableHttpServletRequest interceptGetStyles(MutableHttpServletRequest request);

}
