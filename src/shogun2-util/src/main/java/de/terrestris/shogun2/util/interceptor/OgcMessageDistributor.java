package de.terrestris.shogun2.util.interceptor;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import de.terrestris.shogun2.util.model.Response;

/**
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
@Component
public class OgcMessageDistributor {

	/**
	 * The Logger.
	 */
	private static final Logger LOG = Logger.getLogger(
			OgcMessageDistributor.class);

	/**
	 *
	 */
	private static final String MODIFYING_REQUEST_MSG =
			"Modifying a {0} {1} request";

	/**
	 *
	 */
	private static final String MODIFYING_RESPONSE_MSG =
			"Modifying a {0} {1} response";

	/**
	 *
	 */
	private static final String REQUEST_IMPLEMENTATION_NOT_FOUND_MSG =
			"No interceptor class implementation for request {0} {1} found. " +
			"Forwarding the original request.";

	/**
	 *
	 */
	private static final String RESPONSE_IMPLEMENTATION_NOT_FOUND_MSG =
			"No interceptor class implementation for response {0} {1} found. " +
			"Returning the original response.";

	/**
	 *
	 */
	private static final String REQUEST_NOT_SUPPORTED_MSG = "The request type " +
			"{0} is not supported";

	/**
	 *
	 */
	@Autowired(required = false)
	@Qualifier("wmsRequestInterceptor")
	private WmsRequestInterceptorInterface wmsRequestInterceptor;

	/**
	 *
	 */
	@Autowired(required = false)
	@Qualifier("wfsRequestInterceptor")
	private WfsRequestInterceptorInterface wfsRequestInterceptor;

	/**
	 *
	 */
	@Autowired(required = false)
	@Qualifier("wcsRequestInterceptor")
	private WcsRequestInterceptorInterface wcsRequestInterceptor;

	/**
	 *
	 */
	@Autowired(required = false)
	@Qualifier("wmsResponseInterceptor")
	private WmsResponseInterceptorInterface wmsResponseInterceptor;

	/**
	 *
	 */
	@Autowired(required = false)
	@Qualifier("wfsResponseInterceptor")
	private WfsResponseInterceptorInterface wfsResponseInterceptor;

	/**
	 *
	 */
	@Autowired(required = false)
	@Qualifier("wcsResponseInterceptor")
	private WcsResponseInterceptorInterface wcsResponseInterceptor;

	/**
	 *
	 * @param request
	 * @param message
	 * @return
	 * @throws InterceptorException
	 */
	public MutableHttpServletRequest distributeToRequestInterceptor(
			MutableHttpServletRequest request, OgcMessage message)
					throws InterceptorException {

		if (message.isRequestAllowed()) {
			LOG.debug("Request is ALLOWED, not intercepting the request.");
			return request;
		} else if (message.isRequestDenied()) {
			throw new InterceptorException("Request is DENIED, blocking the request.");
		} else if (message.isRequestModified()) {
			LOG.debug("Request is to be MODIFIED, intercepting the request.");
		}

		String implErrMsg = MessageFormat.format(REQUEST_IMPLEMENTATION_NOT_FOUND_MSG,
				message.getService(), message.getOperation());
		String infoMsg = MessageFormat.format(MODIFYING_REQUEST_MSG,
				message.getService(), message.getOperation());
		String serviceErrMsg = MessageFormat.format(REQUEST_NOT_SUPPORTED_MSG,
				message.getService());
		String operationErrMsg = MessageFormat.format(REQUEST_NOT_SUPPORTED_MSG,
				message.getOperation());

		if (message.isWms()) {

			// check if the wmsRequestInterceptor is available
			if (this.wmsRequestInterceptor == null) {
				LOG.debug(implErrMsg);
				return request;
			}

			LOG.debug(infoMsg);

			if (message.isWmsGetCapabilities()) {
				request = this.wmsRequestInterceptor.interceptGetCapabilities(request);
			} else if (message.isWmsGetMap()) {
				request = this.wmsRequestInterceptor.interceptGetMap(request);
			} else if (message.isWmsGetFeatureInfo()) {
				request = this.wmsRequestInterceptor.interceptGetFeatureInfo(request);
			} else if (message.isWmsGetLegendGraphic()) {
				request = this.wmsRequestInterceptor.interceptGetLegendGraphic(request);
			} else if (message.isWmsGetStyles()) {
				request = this.wmsRequestInterceptor.interceptGetStyles(request);
			} else if (message.isWmsDescribeLayer()) {
				request = this.wmsRequestInterceptor.interceptDescribeLayer(request);
			} else {
				throw new InterceptorException(operationErrMsg);
			}

		} else if (message.isWfs()) {

			// check if the wfsRequestInterceptor is available
			if (this.wfsRequestInterceptor == null) {
				LOG.debug(implErrMsg);
				return request;
			}

			LOG.debug(infoMsg);

			if (message.isWfsGetCapabilities()) {
				request = this.wfsRequestInterceptor.interceptGetCapabilities(request);
			} else if (message.isWfsGetFeature()) {
				request = this.wfsRequestInterceptor.interceptGetFeature(request);
			} else if (message.isWfsDescribeFeatureType()) {
				request = this.wfsRequestInterceptor.interceptDescribeFeatureType(request);
			} else if (message.isWfsTransaction()) {
				request = this.wfsRequestInterceptor.interceptTransaction(request);
			} else if (message.isWfsLockFeature()) {
				request = this.wfsRequestInterceptor.interceptLockFeature(request);
			} else {
				throw new InterceptorException(operationErrMsg);
			}

		} else if (message.isWcs()) {

			// check if the wcsRequestInterceptor is available
			if (this.wcsRequestInterceptor == null) {
				LOG.debug(implErrMsg);
				return request;
			}

			LOG.debug(infoMsg);

			if (message.isWcsGetCapabilities()) {
				request = this.wcsRequestInterceptor.interceptGetCapabilities(request);
			} else if (message.isWcsDescribeCoverage()) {
				request = this.wcsRequestInterceptor.interceptDescribeCoverage(request);
			} else if (message.isWcsGetCoverage()) {
				request = this.wcsRequestInterceptor.interceptGetCoverage(request);
			} else {
				throw new InterceptorException(operationErrMsg);
			}

		} else {
			throw new InterceptorException(serviceErrMsg);
		}

		if (request == null) {
			throw new InterceptorException("The request object is null. " +
					"Please check your RequestInterceptor implementation.");
		}

		return request;

	}

	/**
	 *
	 * @param response
	 * @param message
	 * @return
	 * @throws InterceptorException
	 */
	public Response distributeToResponeInterceptor(Response response,
			OgcMessage message) throws InterceptorException {

		if (message.isResponseAllowed()) {
			LOG.debug("Response is ALLOWED, not intercepting the response.");
			return response;
		} else if (message.isResponseDenied()) {
			throw new InterceptorException("Request is DENIED, blocking the response.");
		} else if (message.isResponseModified()) {
			LOG.debug("Response is to be MODIFIED, intercepting the response.");
		}

		String implErrMsg = MessageFormat.format(REQUEST_IMPLEMENTATION_NOT_FOUND_MSG,
				message.getService(), message.getOperation());
		String infoMsg = MessageFormat.format(MODIFYING_RESPONSE_MSG,
				message.getService(), message.getOperation());
		String serviceErrMsg = MessageFormat.format(REQUEST_NOT_SUPPORTED_MSG,
				message.getService());
		String operationErrMsg = MessageFormat.format(REQUEST_NOT_SUPPORTED_MSG,
				message.getOperation());

		if (message.isWms()) {

			// check if the wmsResponseInterceptor is available
			if (this.wmsResponseInterceptor == null) {
				LOG.debug(implErrMsg);
				return response;
			}

			LOG.debug(infoMsg);

			if (message.isWmsGetCapabilities()) {
				response = this.wmsResponseInterceptor.interceptGetCapabilities(response);
			} else if (message.isWmsGetMap()) {
				response = this.wmsResponseInterceptor.interceptGetMap(response);
			} else if (message.isWmsGetFeatureInfo()) {
				response = this.wmsResponseInterceptor.interceptGetFeatureInfo(response);
			} else if (message.isWmsGetLegendGraphic()) {
				response = this.wmsResponseInterceptor.interceptGetLegendGraphic(response);
			} else if (message.isWmsGetStyles()) {
				response = this.wmsResponseInterceptor.interceptGetStyles(response);
			} else if (message.isWmsDescribeLayer()) {
				response = this.wmsResponseInterceptor.interceptDescribeLayer(response);
			} else {
				throw new InterceptorException(operationErrMsg);
			}

		} else if (message.isWfs()) {

			// check if the wfsResponseInterceptor is available
			if (this.wfsResponseInterceptor == null) {
				LOG.debug(implErrMsg);
				return response;
			}

			LOG.debug(infoMsg);

			if (message.isWfsGetCapabilities()) {
				response = this.wfsResponseInterceptor.interceptGetCapabilities(response);
			} else if (message.isWfsGetFeature()) {
				response = this.wfsResponseInterceptor.interceptGetFeature(response);
			} else if (message.isWfsDescribeFeatureType()) {
				response = this.wfsResponseInterceptor.interceptDescribeFeatureType(response);
			} else if (message.isWfsTransaction()) {
				response = this.wfsResponseInterceptor.interceptTransaction(response);
			} else if (message.isWfsLockFeature()) {
				response = this.wfsResponseInterceptor.interceptLockFeature(response);
			} else {
				throw new InterceptorException(operationErrMsg);
			}

		} else if (message.isWcs()) {

			// check if the wcsResponseInterceptor is available
			if (this.wcsResponseInterceptor == null) {
				LOG.debug(implErrMsg);
				return response;
			}

			LOG.debug(infoMsg);

			if (message.isWcsGetCapabilities()) {
				response = this.wcsResponseInterceptor.interceptGetCapabilities(response);
			} else if (message.isWcsDescribeCoverage()) {
				response = this.wcsResponseInterceptor.interceptDescribeCoverage(response);
			} else if (message.isWcsGetCoverage()) {
				response = this.wcsResponseInterceptor.interceptGetCoverage(response);
			} else {
				throw new InterceptorException(operationErrMsg);
			}

		} else {
			throw new InterceptorException(serviceErrMsg);
		}

		if (response == null) {
			throw new InterceptorException("The response object is null. " +
					"Please check your RequestInterceptor implementation.");
		}

		return response;
	}

	/**
	 * @param wmsRequestInterceptor the wmsRequestInterceptor to set
	 */
	public void setWmsRequestInterceptor(
			WmsRequestInterceptorInterface wmsRequestInterceptor) {
		this.wmsRequestInterceptor = wmsRequestInterceptor;
	}

	/**
	 * @param wfsRequestInterceptor the wfsRequestInterceptor to set
	 */
	public void setWfsRequestInterceptor(
			WfsRequestInterceptorInterface wfsRequestInterceptor) {
		this.wfsRequestInterceptor = wfsRequestInterceptor;
	}

	/**
	 * @param wcsRequestInterceptor the wcsRequestInterceptor to set
	 */
	public void setWcsRequestInterceptor(
			WcsRequestInterceptorInterface wcsRequestInterceptor) {
		this.wcsRequestInterceptor = wcsRequestInterceptor;
	}

	/**
	 * @param wmsResponseInterceptor the wmsResponseInterceptor to set
	 */
	public void setWmsResponseInterceptor(
			WmsResponseInterceptorInterface wmsResponseInterceptor) {
		this.wmsResponseInterceptor = wmsResponseInterceptor;
	}

	/**
	 * @param wfsResponseInterceptor the wfsResponseInterceptor to set
	 */
	public void setWfsResponseInterceptor(
			WfsResponseInterceptorInterface wfsResponseInterceptor) {
		this.wfsResponseInterceptor = wfsResponseInterceptor;
	}

	/**
	 * @param wcsResponseInterceptor the wcsResponseInterceptor to set
	 */
	public void setWcsResponseInterceptor(
			WcsResponseInterceptorInterface wcsResponseInterceptor) {
		this.wcsResponseInterceptor = wcsResponseInterceptor;
	}
}
