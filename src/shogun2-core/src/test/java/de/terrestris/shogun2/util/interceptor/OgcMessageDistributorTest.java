package de.terrestris.shogun2.util.interceptor;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;

import de.terrestris.shogun2.util.enumeration.InterceptorEnum;
import de.terrestris.shogun2.util.enumeration.OgcEnum;
import de.terrestris.shogun2.util.model.Response;

public class OgcMessageDistributorTest {

	@Autowired
	private OgcMessageDistributor distributor;

	public static final String ENDPOINT = "bvb:shinji";

	@Before
	public void set_up() {
		distributor = new OgcMessageDistributor();
	}

	@Test
	public void intercept_request_allowed() throws InterceptorException {

		OgcMessage message = new OgcMessage(null, null, null, InterceptorEnum.RuleType.ALLOW, null);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

		MutableHttpServletRequest returnedRequest =
				distributor.distributeToRequestInterceptor(mutableRequest, message);

		assertNotNull(returnedRequest);
	}

	@Test(expected=InterceptorException.class)
	public void intercept_request_denied() throws InterceptorException {

		OgcMessage message = new OgcMessage(null, null, null, InterceptorEnum.RuleType.DENY, null);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

		distributor.distributeToRequestInterceptor(mutableRequest, message);
	}

	@Test
	public void intercept_request_modified_no_implementation() throws InterceptorException {

		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WMS, OgcEnum.OperationType.GET_MAP,
				ENDPOINT, InterceptorEnum.RuleType.MODIFY, null);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

		MutableHttpServletRequest returnedRequest =
				distributor.distributeToRequestInterceptor(mutableRequest, message);

		assertNotNull(returnedRequest);
	}

	@Test
	public void intercept_response_allowed() throws InterceptorException {

		OgcMessage message = new OgcMessage(null, null, null, null, InterceptorEnum.RuleType.ALLOW);

		Response response = new Response();

		Response returnedRequest =
				distributor.distributeToResponseInterceptor(response, message);

		assertNotNull(returnedRequest);
	}

	@Test(expected=InterceptorException.class)
	public void intercept_response_denied() throws InterceptorException {

		OgcMessage message = new OgcMessage(null, null, null, null, InterceptorEnum.RuleType.DENY);

		Response response = new Response();

		distributor.distributeToResponseInterceptor(response, message);
	}

	@Test
	public void intercept_response_modified_no_implementation() throws InterceptorException {

		OgcMessage message = new OgcMessage(OgcEnum.ServiceType.WMS, OgcEnum.OperationType.GET_MAP,
				ENDPOINT, null, InterceptorEnum.RuleType.MODIFY);

		Response response = new Response();

		Response returnedResponse =
				distributor.distributeToResponseInterceptor(response, message);

		assertNotNull(returnedResponse);
	}

}
