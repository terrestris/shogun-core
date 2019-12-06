package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.web.EndpointDocController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Set;

/**
 * Service class for the {@link EndpointDocController}.
 *
 * @author Christian Mayer
 */
@Service("endpointDocService")
public class EndpointDocService {

    /**
     * Returns all RequestMappingInfo instances from type and
     * method-level @RequestMapping annotations in @Controller classes.
     *
     * @param requestMappingHandlerMapping The RequestMappingInfo collection of Spring
     * @return A set of RequestMappingInfo
     */
    @PreAuthorize("hasRole(@configHolder.getSuperAdminRoleName())")
    public Set<RequestMappingInfo> getEndpoints(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return requestMappingHandlerMapping.getHandlerMethods().keySet();
    }

}
