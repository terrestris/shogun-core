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
 * Class representing the layer name 
 * 
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class GeoWebServiceLayerName extends PersistentObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String layerName;

	/**
	 * 
	 */
	public GeoWebServiceLayerName() {
		super();
	}

	/**
	 * @param layerName
	 */
	public GeoWebServiceLayerName(String layerName) {
		super();
		this.layerName = layerName;
	}

	/**
	 * @return the layerName
	 */
	public String getLayerName() {
		return layerName;
	}

	/**
	 * @param layerName the layerName to set
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
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
		return new HashCodeBuilder(67, 13).
				appendSuper(super.hashCode()).
				append(getLayerName()).
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
		if (!(obj instanceof GeoWebServiceLayerName))
			return false;
		GeoWebServiceLayerName other = (GeoWebServiceLayerName) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getLayerName(), other.getLayerName()).
				isEquals();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
	
}
