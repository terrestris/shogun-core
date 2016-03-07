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
	 * E.g. 'WMS'
	 */
	private String service;

	/**
	 * E.g. 'GetMap'
	 */
	private String operation;

	/**
	 * E.g. 'workspace:layername'
	 */
	private String endPoint;

	/**
	 *
	 */
	private String requestRule;

	/**
	 *
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
		return (this.getService().equalsIgnoreCase(OgcNaming.SERVICE_WMS)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfs() {
		return (this.getService().equalsIgnoreCase(OgcNaming.SERVICE_WFS)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWcs() {
		return (this.getService().equalsIgnoreCase(OgcNaming.SERVICE_WCS)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsGetCapabilities() {
		return (this.isWms() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_CAPABILITIES)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsGetMap() {
		return (this.isWms() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_MAP)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsGetFeatureInfo() {
		return (this.isWms() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_FEATURE_INFO)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsDescribeLayer() {
		return (this.isWms() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_DESCRIBE_LAYER)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsGetLegendGraphic() {
		return (this.isWms() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_LEGEND_GRAPHIC)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWmsGetStyles() {
		return (this.isWms() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_STYLES)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfsGetCapabilities() {
		return (this.isWfs() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_CAPABILITIES)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfsDescribeFeatureType() {
		return (this.isWfs() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_DESCRIBE_FEATURE_TYPE)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfsGetFeature() {
		return (this.isWfs() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_FEATURE)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfsLockFeature() {
		return (this.isWfs() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_LOCK_FEATURE)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWfsTransaction() {
		return (this.isWfs() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_TRANSACTION)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWcsGetCapabilities() {
		return (this.isWcs() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_CAPABILITIES)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWcsDescribeCoverage() {
		return (this.isWcs() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_DESCRIBE_COVERAGE)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isWcsGetCoverage() {
		return (this.isWcs() &&
				this.getOperation().equalsIgnoreCase(OgcNaming.OPERATION_GET_COVERAGE)) ?
				true : false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isRequestAllowed() {
		return this.getRequestRule().equalsIgnoreCase("ALLOW");
	}

	/**
	 *
	 * @return
	 */
	public boolean isResponseAllowed() {
		return this.getResponseRule().equalsIgnoreCase("ALLOW");
	}

	/**
	 *
	 * @return
	 */
	public boolean isRequestDenied() {
		return this.getRequestRule().equalsIgnoreCase("DENY");
	}

	/**
	 *
	 * @return
	 */
	public boolean isResponseDenied() {
		return this.getResponseRule().equalsIgnoreCase("DENY");
	}

	/**
	 *
	 * @return
	 */
	public boolean isRequestModified() {
		return this.getRequestRule().equalsIgnoreCase("MODIFY");
	}

	/**
	 *
	 * @return
	 */
	public boolean isResponseModified() {
		return this.getResponseRule().equalsIgnoreCase("MODIFY");
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
				.appendSuper(super.equals(other))
				.append(getService(), other.getService())
				.append(getOperation(), other.getOperation())
				.append(getEndPoint(), other.getEndPoint())
				.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("service", getService())
				.append("operation", getOperation())
				.append("endPoint", getEndPoint())
				.append("requestRule", getRequestRule())
				.append("responseRule", getResponseRule())
				.toString();
	}

}
