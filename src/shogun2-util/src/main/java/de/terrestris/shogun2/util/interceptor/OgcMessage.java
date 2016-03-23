package de.terrestris.shogun2.util.interceptor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class OgcMessage {

	/**
	 * The OGC service type, e.g. WMS, WFS or WCS.
	 */
	private String service;

	/**
	 * The OGC operation type, e.g. GetMap.
	 */
	private String operation;

	/**
	 * The OGC/GeoServer endPoint (a generalization for layer, featureType,
	 * coverage or namespace), e.g. SHOGUN:SHINJI.
	 */
	private String endPoint;

	/**
	 * The rule type for this request, possible rules are:
	 *   * ALLOW
	 *   * DENY
	 *   * MODIFY
	 */
	private String requestRule;

	/**
	 * The rule type for this response, possible rules are:
	 *   * ALLOW
	 *   * DENY
	 *   * MODIFY
	 */
	private String responseRule;

	/**
	 * Default constructor
	 */
	public OgcMessage() {
	}


	/**
	 *
	 * @param service
	 * @param operation
	 * @param endPoint
	 * @param requestRule
	 * @param responseRule
	 */
	public OgcMessage(String service, String operation, String endPoint,
			String requestRule, String responseRule) {
		this.service = service;
		this.operation = operation;
		this.endPoint = endPoint;
		this.requestRule = requestRule;
		this.responseRule = responseRule;
	}

	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * @return the endPoint
	 */
	public String getEndPoint() {
		return endPoint;
	}

	/**
	 * @param endPoint the endPoint to set
	 */
	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * @return the requestRule
	 */
	public String getRequestRule() {
		return requestRule;
	}

	/**
	 * @param requestRule the requestRule to set
	 */
	public void setRequestRule(String requestRule) {
		this.requestRule = requestRule;
	}

	/**
	 * @return the responseRule
	 */
	public String getResponseRule() {
		return responseRule;
	}

	/**
	 * @param responseRule the responseRule to set
	 */
	public void setResponseRule(String responseRule) {
		this.responseRule = responseRule;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWms() {
		return this.getService() != null &&
				this.getService().equalsIgnoreCase(OgcNaming.SERVICE_WMS);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfs() {
		return this.getService() != null &&
				this.getService().equalsIgnoreCase(OgcNaming.SERVICE_WFS);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWcs() {
		return this.getService() != null &&
				this.getService().equalsIgnoreCase(OgcNaming.SERVICE_WCS);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsGetCapabilities() {
		return this.isWms() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_CAPABILITIES);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsGetMap() {
		return this.isWms() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_MAP);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsGetFeatureInfo() {
		return this.isWms() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_FEATURE_INFO);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsDescribeLayer() {
		return this.isWms() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_DESCRIBE_LAYER);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsGetLegendGraphic() {
		return this.isWms() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_LEGEND_GRAPHIC);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsGetStyles() {
		return this.isWms() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_STYLES);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfsGetCapabilities() {
		return this.isWfs() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_CAPABILITIES);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfsDescribeFeatureType() {
		return this.isWfs() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_DESCRIBE_FEATURE_TYPE);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfsGetFeature() {
		return this.isWfs() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_FEATURE);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfsLockFeature() {
		return this.isWfs() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_LOCK_FEATURE);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfsTransaction() {
		return this.isWfs() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_TRANSACTION);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWcsGetCapabilities() {
		return this.isWcs() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_CAPABILITIES);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWcsDescribeCoverage() {
		return this.isWcs() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_DESCRIBE_COVERAGE);
	}

	/**
	 *
	 * @return
	 */
	public boolean isWcsGetCoverage() {
		return this.isWcs() &&
				this.getOperation() != null &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_COVERAGE);
	}

	/**
	 *
	 * @return
	 */
	public boolean isRequestAllowed() {
		return this.getRequestRule() != null &&
				this.getRequestRule().equalsIgnoreCase("ALLOW");
	}

	/**
	 *
	 * @return
	 */
	public boolean isResponseAllowed() {
		return this.getResponseRule() != null &&
				this.getResponseRule().equalsIgnoreCase("ALLOW");
	}

	/**
	 *
	 * @return
	 */
	public boolean isRequestDenied() {
		return this.getRequestRule() != null &&
				this.getRequestRule().equalsIgnoreCase("DENY");
	}

	/**
	 *
	 * @return
	 */
	public boolean isResponseDenied() {
		return this.getResponseRule() != null &&
				this.getResponseRule().equalsIgnoreCase("DENY");
	}

	/**
	 *
	 * @return
	 */
	public boolean isRequestModified() {
		return this.getRequestRule() != null &&
				this.getRequestRule().equalsIgnoreCase("MODIFY");
	}

	/**
	 *
	 * @return
	 */
	public boolean isResponseModified() {
		return this.getResponseRule() != null &&
				this.getResponseRule().equalsIgnoreCase("MODIFY");
	}

	/**
	 *
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof OgcMessage))
			return false;
		OgcMessage other = (OgcMessage) obj;

		return new EqualsBuilder()
				.append(getService(), other.getService())
				.append(getOperation(), other.getOperation())
				.append(getEndPoint(), other.getEndPoint())
				.append(getRequestRule(), other.getRequestRule())
				.append(getResponseRule(), other.getResponseRule())
				.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("service", getService())
				.append("operation", getOperation())
				.append("endPoint", getEndPoint())
				.append("requestRule", getRequestRule())
				.append("responseRule", getResponseRule())
				.toString();
	}

}
