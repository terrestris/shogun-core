package de.terrestris.shogun2.model.interceptor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * The model representing the rules for the GeoServer Interceptor class.
 *
 * A rule is following the schema:
 *
 * <event>.<service>.<operation>.<endPoint>=<rule>
 *
 * The rules will be evaluated for every request where we determine the most
 * specific rule to apply.
 *
 * Allowed values for the rule are ALLOW, DENY, MODIFY.
 * Allowed values for the event are REQUEST and RESPONSE.
 *
 * @author Daniel Koch
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class InterceptorRule extends PersistentObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The event for this rule, possible values are:
	 *   * REQUEST
	 *   * RESPONSE
	 */
	@Column(nullable = false)
	private String event;

	/**
	 * The rule type for this rule, possible rules are:
	 *   * ALLOW
	 *   * DENY
	 *   * MODIFY
	 */
	@Column(nullable = false)
	private String rule;

	/**
	 * The OGC service type, e.g. WMS, WFS or WCS.
	 */
	@Column(nullable = false)
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
	 *
	 */
	public InterceptorRule() {
	}

	/**
	 *
	 * @param rule
	 * @param service
	 * @param operation
	 * @param endPoint
	 */
	public InterceptorRule(String event, String rule, String service,
			String operation, String endPoint) {
		this.event = event;
		this.rule = rule;
		this.service = service;
		this.operation = operation;
		this.endPoint = endPoint;
	}

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return the rule
	 */
	public String getRule() {
		return rule;
	}

	/**
	 * @param rule the rule to set
	 */
	public void setRule(String rule) {
		this.rule = rule;
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
	 * @see java.lang.Object#hashCode()
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
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
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InterceptorRule))
			return false;
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
