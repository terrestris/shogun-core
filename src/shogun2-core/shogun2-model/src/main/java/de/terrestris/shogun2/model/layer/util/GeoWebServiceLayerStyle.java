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
 * Class representing the layer style
 * 
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class GeoWebServiceLayerStyle extends PersistentObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String styleName;

	/**
	 * 
	 */
	public GeoWebServiceLayerStyle() {
		super();
	}

	/**
	 * @param styleName
	 */
	public GeoWebServiceLayerStyle(String styleName) {
		super();
		this.styleName = styleName;
	}

	/**
	 * @return the styleName
	 */
	public String getStyleName() {
		return styleName;
	}

	/**
	 * @param styleName the styleName to set
	 */
	public void setStyleName(String styleName) {
		this.styleName = styleName;
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
		return new HashCodeBuilder(71, 13).
				appendSuper(super.hashCode()).
				append(getStyleName()).
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
		if (!(obj instanceof GeoWebServiceLayerStyle))
			return false;
		GeoWebServiceLayerStyle other = (GeoWebServiceLayerStyle) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getStyleName(), other.getStyleName()).
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
