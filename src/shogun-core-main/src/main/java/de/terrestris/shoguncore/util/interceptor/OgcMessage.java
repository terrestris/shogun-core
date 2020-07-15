package de.terrestris.shoguncore.util.interceptor;

import de.terrestris.shoguncore.util.enumeration.InterceptorEnum;
import de.terrestris.shoguncore.util.enumeration.OgcEnum;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
public class OgcMessage {

    /**
     * The OGC service type, possible rules are:
     * * WMS
     * * WMTS
     * * WFS
     * * WCS
     */
    @Enumerated(EnumType.STRING)
    private OgcEnum.ServiceType service;

    /**
     * The OGC operation type, e.g. GetMap.
     */
    @Enumerated(EnumType.STRING)
    private OgcEnum.OperationType operation;

    /**
     * The OGC/GeoServer endPoint (a generalization for layer, featureType,
     * coverage or namespace), e.g. SHOGUN:SHINJI.
     */
    private String endPoint;

    /**
     * The rule type for this request, possible rules are:
     * * ALLOW
     * * DENY
     * * MODIFY
     */
    @Enumerated(EnumType.STRING)
    private InterceptorEnum.RuleType requestRule;

    /**
     * The rule type for this response, possible rules are:
     * * ALLOW
     * * DENY
     * * MODIFY
     */
    @Enumerated(EnumType.STRING)
    private InterceptorEnum.RuleType responseRule;

    /**
     * Default constructor
     */
    public OgcMessage() {
    }

    /**
     * @param service
     * @param operation
     * @param endPoint
     * @param requestRule
     * @param responseRule
     */
    public OgcMessage(OgcEnum.ServiceType service, OgcEnum.OperationType operation,
                      String endPoint, InterceptorEnum.RuleType requestRule,
                      InterceptorEnum.RuleType responseRule) {
        this.service = service;
        this.operation = operation;
        this.endPoint = endPoint;
        this.requestRule = requestRule;
        this.responseRule = responseRule;
    }

    /**
     * @return the service
     */
    public OgcEnum.ServiceType getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(OgcEnum.ServiceType service) {
        this.service = service;
    }

    /**
     * @return the operation
     */
    public OgcEnum.OperationType getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(OgcEnum.OperationType operation) {
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
    public InterceptorEnum.RuleType getRequestRule() {
        return requestRule;
    }

    /**
     * @param requestRule the requestRule to set
     */
    public void setRequestRule(InterceptorEnum.RuleType requestRule) {
        this.requestRule = requestRule;
    }

    /**
     * @return the responseRule
     */
    public InterceptorEnum.RuleType getResponseRule() {
        return responseRule;
    }

    /**
     * @param responseRule the responseRule to set
     */
    public void setResponseRule(InterceptorEnum.RuleType responseRule) {
        this.responseRule = responseRule;
    }

    /**
     * @return
     */
    public boolean isWms() {
        return this.getService() != null &&
            this.getService().equals(OgcEnum.ServiceType.WMS);
    }

    /**
     * @return
     */
    public boolean isWmts() {
        return this.getService() != null &&
            this.getService().equals(OgcEnum.ServiceType.WMTS);
    }

    /**
     * @return
     */
    public boolean isWfs() {
        return this.getService() != null &&
            this.getService().equals(OgcEnum.ServiceType.WFS);
    }

    /**
     * @return
     */
    public boolean isWcs() {
        return this.getService() != null &&
            this.getService().equals(OgcEnum.ServiceType.WCS);
    }

    /**
     * @return
     */
    public boolean isWps() {
        return this.getService() != null &&
            this.getService().equals(OgcEnum.ServiceType.WPS);
    }

    /**
     * @return
     */
    public boolean isW3ds() {
        return this.getService() != null &&
            this.getService().equals(OgcEnum.ServiceType.W3DS);
    }

    /**
     * @return
     */
    public boolean isWmsGetCapabilities() {
        return this.isWms() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_CAPABILITIES);
    }

    /**
     * @return
     */
    public boolean isWmsGetMap() {
        return this.isWms() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_MAP);
    }

    /**
     * @return
     */
    public boolean isWmsGetFeatureInfo() {
        return this.isWms() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_FEATURE_INFO);
    }

    /**
     * @return
     */
    public boolean isWmsDescribeLayer() {
        return this.isWms() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.DESCRIBE_LAYER);
    }

    /**
     * @return
     */
    public boolean isWmsGetLegendGraphic() {
        return this.isWms() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_LEGEND_GRAPHIC);
    }

    /**
     * @return
     */
    public boolean isWmsGetStyles() {
        return this.isWms() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_STYLES);
    }

    /**
     * @return
     */
    public boolean isWmtsGetCapabilities() {
        return this.isWmts() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_CAPABILITIES);
    }

    /**
     * @return
     */
    public boolean isWmtsGetTile() {
        return this.isWmts() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_TILE);
    }

    /**
     * @return
     */
    public boolean isWmtsGetFeatureInfo() {
        return this.isWmts() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_FEATURE_INFO);
    }

    /**
     * @return
     */
    public boolean isWfsGetCapabilities() {
        return this.isWfs() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_CAPABILITIES);
    }

    /**
     * @return
     */
    public boolean isWfsDescribeFeatureType() {
        return this.isWfs() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.DESCRIBE_FEATURE_TYPE);
    }

    /**
     * @return
     */
    public boolean isWfsGetFeature() {
        return this.isWfs() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_FEATURE);
    }

    /**
     * @return
     */
    public boolean isWfsLockFeature() {
        return this.isWfs() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.LOCK_FEATURE);
    }

    /**
     * @return
     */
    public boolean isWfsTransaction() {
        return this.isWfs() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.TRANSACTION);
    }

    /**
     * @return
     */
    public boolean isWcsGetCapabilities() {
        return this.isWcs() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_CAPABILITIES);
    }

    /**
     * @return
     */
    public boolean isWcsDescribeCoverage() {
        return this.isWcs() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.DESCRIBE_COVERAGE);
    }

    /**
     * @return
     */
    public boolean isWcsGetCoverage() {
        return this.isWcs() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_COVERAGE);
    }

    /**
     * @return
     */
    public boolean isWpsGetCapabilities() {
        return this.isWps() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_CAPABILITIES);
    }

    /**
     * @return
     */
    public boolean isWpsDescribeProcess() {
        return this.isWps() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.DESCRIBE_PROCESS);
    }

    /**
     * @return
     */
    public boolean isWpsExecute() {
        return this.isWps() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.EXECUTE);
    }

    /**
     * @return
     */
    public boolean isW3dsGetCapabilities() {
        return this.isW3ds() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_CAPABILITIES);
    }

    /**
     * @return
     */
    public boolean isW3dsGetScene() {
        return this.isW3ds() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_SCENE);
    }

    /**
     * @return
     */
    public boolean isW3dsGetFeatureInfo() {
        return this.isW3ds() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_FEATURE_INFO);
    }

    /**
     * @return
     */
    public boolean isW3dsGetLayerInfo() {
        return this.isW3ds() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_LAYER_INFO);
    }

    /**
     * @return
     */
    public boolean isW3dsGetTile() {
        return this.isW3ds() &&
            this.getOperation() != null &&
            this.getOperation().equals(OgcEnum.OperationType.GET_TILE);
    }

    /**
     * @return
     */
    public boolean isRequestAllowed() {
        return this.getRequestRule() != null &&
            this.getRequestRule().equals(InterceptorEnum.RuleType.ALLOW);
    }

    /**
     * @return
     */
    public boolean isResponseAllowed() {
        return this.getResponseRule() != null &&
            this.getResponseRule().equals(InterceptorEnum.RuleType.ALLOW);
    }

    /**
     * @return
     */
    public boolean isRequestDenied() {
        return this.getRequestRule() != null &&
            this.getRequestRule().equals(InterceptorEnum.RuleType.DENY);
    }

    /**
     * @return
     */
    public boolean isResponseDenied() {
        return this.getResponseRule() != null &&
            this.getResponseRule().equals(InterceptorEnum.RuleType.DENY);
    }

    /**
     * @return
     */
    public boolean isRequestModified() {
        return this.getRequestRule() != null &&
            this.getRequestRule().equals(InterceptorEnum.RuleType.MODIFY);
    }

    /**
     * @return
     */
    public boolean isResponseModified() {
        return this.getResponseRule() != null &&
            this.getResponseRule().equals(InterceptorEnum.RuleType.MODIFY);
    }

    /**
     *
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OgcMessage)) {
            return false;
        }
        OgcMessage other = (OgcMessage) obj;

        return new EqualsBuilder()
            .append(getService(), other.getService())
            .append(getOperation(), other.getOperation())
            .append(getEndPoint(), other.getEndPoint())
            .append(getRequestRule(), other.getRequestRule())
            .append(getResponseRule(), other.getResponseRule())
            .isEquals();
    }

    @Override
    public int hashCode() {
        // two randomly chosen prime numbers
        return new HashCodeBuilder(83, 61).
            appendSuper(super.hashCode())
            .append(getService())
            .append(getOperation())
            .append(getEndPoint())
            .append(getRequestRule())
            .append(getResponseRule())
            .toHashCode();
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
