package de.terrestris.shogun2.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import de.terrestris.shogun2.service.EndpointDocService;

/**
 * Web-controller for endpoint documentation.
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
	@Qualifier("handlerMapping")
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	/**
	 * The service layer instance
	 */
	@Autowired
	@Qualifier("endpointDocService")
	private EndpointDocService service;

	/**
	 * Provides an overview of all mapped endpoints.
	 */
	@RequestMapping(value = "/endpointdoc", method = RequestMethod.GET)
	public @ResponseBody Set<RequestMappingInfo> getEndpoints() {

		return this.service.getEndpoints(requestMappingHandlerMapping);
	}


	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(EndpointDocService service) {
		this.service = service;
	}

	/**
	 * @param requestMappingHandlerMapping
	 *            the requestMappingHandlerMapping to set
	 */
	public void setRequestMappingHandlerMapping(RequestMappingHandlerMapping requestMappingHandlerMapping) {
		this.requestMappingHandlerMapping = requestMappingHandlerMapping;
	}

}
