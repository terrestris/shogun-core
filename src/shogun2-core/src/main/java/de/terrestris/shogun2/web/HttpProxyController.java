package de.terrestris.shogun2.web;

import de.terrestris.shogun2.service.HttpProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Controller for simple HTTP Proxy service (forward proxy)
 *
 * @author Andre Henn
 * @author terrestris GmbH & co. KG
 */
@Controller
public class HttpProxyController {

	@Autowired
	@Qualifier("httpProxyService")
	private HttpProxyService proxyService;

	/**
	 * Web controller mapping <i>proxy.action</i> to doProxy method. Provided parameters are passed to {@link HttpProxyService}
	 * @param request {@link HttpServletRequest} to use in proxy (e.g. to obtain headers from)
	 * @param baseUrl The base url of request
	 * @param params Request params
	 * @return ResponseEntity
	 */
	@RequestMapping("/proxy.action")
	public @ResponseBody ResponseEntity<?> doProxy(HttpServletRequest request, @RequestParam String baseUrl, @RequestParam(required = false) Map<String, String> params){
		return proxyService.doProxy(request, baseUrl, params);
	}

	/**
	 * The setter method of {@link HttpProxyService}
	 * @param proxyService {@link HttpProxyService} to set
	 */
	public void setProxyService(HttpProxyService proxyService) {
		this.proxyService = proxyService;
	}
}
