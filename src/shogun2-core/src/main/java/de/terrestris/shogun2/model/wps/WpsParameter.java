package de.terrestris.shogun2.model.wps;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.terrestris.shogun2.model.PersistentObject;

/**
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		property = "classType"
)
@JsonSubTypes({
		@Type(value = WpsPrimitive.class, name = "WpsPrimitive"),
		@Type(value = WpsReference.class, name = "WpsReference"),
		@Type(value = WpsProcessExecute.class, name = "WpsProcessExecute")
})
public abstract class WpsParameter extends PersistentObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private final String classType;

	/**
	 *
	 */
	private String valueType;

	/**
	 * Constructor
	 */
	public WpsParameter() {
		this.classType = getClass().getSimpleName();
	}


	/**
	 * @return the classType
	 */
	public String getClassType() {
		return classType;
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
