package de.terrestris.shogun2.model.wps;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * 
 */
@Entity
@JsonTypeInfo(
	use = JsonTypeInfo.Id.MINIMAL_CLASS,
	include = JsonTypeInfo.As.PROPERTY,
	property = "classType",
	visible = true
)
//@JsonSubTypes({
//		@Type(value = PointGeometry.class, name = "Point"),
//		@Type(value = LineGeometry.class, name = "LineString"),
//		@Type(value = PolygonGeometry.class, name = "Polygon")
//})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class WpsParameter extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private String valueType;

	/**
	 * Constructor
	 */
	public WpsParameter() {
	}


	/**
	 * @return the valueType
	 */
	public String getValueType() {
		return valueType;
	}


	/**
	 * @param valueType the valueType to set
	 */
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 *
	 * According to
	 * http://stackoverflow.com/questions/27581/overriding-equals-and-hashcode-in-java
	 * it is recommended only to use getter-methods when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(19, 47) // two randomly chosen prime numbers
			.appendSuper(super.hashCode())
			.append(getValueType())
			.toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 * According to
	 * http://stackoverflow.com/questions/27581/overriding-equals-and-hashcode-in-java
	 * it is recommended only to use getter-methods when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WpsParameter))
			return false;
		WpsParameter other = (WpsParameter) obj;

		return new EqualsBuilder()
			.appendSuper(super.equals(other))
			.append(getValueType(), other.getValueType())
			.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString(){
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.appendSuper(super.toString())
			.append("valueType", valueType)
			.toString();
	}
}
