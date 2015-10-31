/**
 * 
 */
package de.terrestris.shogun2.model.layer.util;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * 
 * Class representing scale / resolution
 * 
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table(name = "RESOLUTION")
public class Resolution extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double resolution;
	
	/**
	 * 
	 */
	public Resolution() {
		super();
	}
	
	/**
	 * 
	 * @param res
	 */
	public Resolution(Double res) {
		super();
		this.resolution = res;
	}

	/**
	 * @return the resolution
	 */
	public Double getResolution() {
		return resolution;
	}

	/**
	 * @param resolution the resolution to set
	 */
	public void setResolution(Double resolution) {
		this.resolution = resolution;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(73, 13).
				appendSuper(super.hashCode()).
				append(getResolution()).
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
	public boolean equals(Object obj) {
		if (!(obj instanceof Resolution))
			return false;
		Resolution other = (Resolution) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getResolution(), other.getResolution()).
				isEquals();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}