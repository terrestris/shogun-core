package de.terrestris.shogun2.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 
 * @author Christian mayer
 */
@Controller
public class EndpointDocController {

	/**
	 * Creates RequestMappingInfo instances from type and
	 * method-level @RequestMapping annotations in @Controller classes.
	 */
	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;


	/**
	 * Provides an overview of all mapped endpoints.
	 */
	@RequestMapping(value = "/endpointdoc", method = RequestMethod.GET)
	public @ResponseBody Set<RequestMappingInfo> getEndpoints() {

		if (requestMappingHandlerMapping.getHandlerMethods() != null) {
			return requestMappingHandlerMapping.getHandlerMethods().keySet();
		}
		
		return null;
	}


	/**
	 * @param requestMappingHandlerMapping the requestMappingHandlerMapping to set
	 */
	public void setRequestMappingHandlerMapping(RequestMappingHandlerMapping requestMappingHandlerMapping) {
		this.requestMappingHandlerMapping = requestMappingHandlerMapping;
	}
}
