package de.terrestris.shoguncore.model.interceptor;

import de.terrestris.shoguncore.model.PersistentObject;
import de.terrestris.shoguncore.util.enumeration.HttpEnum;
import de.terrestris.shoguncore.util.enumeration.InterceptorEnum;
import de.terrestris.shoguncore.util.enumeration.OgcEnum;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

/**
 * The model representing the rules for the GeoServer Interceptor class.
 * <p>
 * A rule is following the schema:
 * <p>
 * <event>.<service>.<operation>.<endPoint>=<rule>
 * <p>
 * The rules will be evaluated for every request where we determine the most
 * specific rule to apply.
 * <p>
 * Allowed values for the rule are ALLOW, DENY, MODIFY.
 * Allowed values for the event are REQUEST and RESPONSE.
 *
 * @author Daniel Koch
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 */
@Entity
@Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InterceptorRule extends PersistentObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The event for this rule, possible values are:
     * * REQUEST
     * * RESPONSE
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HttpEnum.EventType event;

    /**
     * The rule type for this rule, possible rules are:
     * * ALLOW
     * * DENY
     * * MODIFY
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InterceptorEnum.RuleType rule;

    /**
     * The OGC service type, possible rules are:
     * * WMS
     * * WFS
     * * WCS
     */
    @Column(nullable = false)
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
     *
     */
    public InterceptorRule() {
    }

    /**
     * @param rule
     * @param service
     * @param operation
     * @param endPoint
     */
    public InterceptorRule(HttpEnum.EventType event, InterceptorEnum.RuleType rule,
                           OgcEnum.ServiceType service, OgcEnum.OperationType operation, String endPoint) {
        this.event = event;
        this.rule = rule;
        this.service = service;
        this.operation = operation;
        this.endPoint = endPoint;
    }

    /**
     * @return the event
     */
    public HttpEnum.EventType getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(HttpEnum.EventType event) {
        this.event = event;
    }

    /**
     * @return the rule
     */
    public InterceptorEnum.RuleType getRule() {
        return rule;
    }

    /**
     * @param rule the rule to set
     */
    public void setRule(InterceptorEnum.RuleType rule) {
        this.rule = rule;
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
     * @see java.lang.Object#hashCode()
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
     */
    @Override
    public int hashCode() {
        // two randomly chosen prime numbers
        return new HashCodeBuilder(19, 89).
            appendSuper(super.hashCode()).
            append(getEvent()).
            append(getRule()).
            append(getService()).
            append(getOperation()).
            append(getEndPoint()).
            toHashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof InterceptorRule)) {
            return false;
        }
        InterceptorRule other = (InterceptorRule) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getEvent(), other.getEvent()).
            append(getRule(), other.getRule()).
            append(getService(), other.getService()).
            append(getOperation(), other.getOperation()).
            append(getEndPoint(), other.getEndPoint()).
            isEquals();
    }

    /**
     *
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("event", getEvent())
            .append("rule", getRule())
            .append("service", getService())
            .append("operation", getOperation())
            .append("endPoint", getEndPoint())
            .toString();
    }
}
