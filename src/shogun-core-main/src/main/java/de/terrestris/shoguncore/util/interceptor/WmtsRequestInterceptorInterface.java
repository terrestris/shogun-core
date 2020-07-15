package de.terrestris.shoguncore.util.interceptor;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public interface WmtsRequestInterceptorInterface {

    MutableHttpServletRequest interceptGetTile(MutableHttpServletRequest request, HashMap<String, Optional<String>> optionals);

    MutableHttpServletRequest interceptGetCapabilities(MutableHttpServletRequest request);

    MutableHttpServletRequest interceptGetFeatureInfo(MutableHttpServletRequest request, HashMap<String, Optional<String>> optionals);

}
