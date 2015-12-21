/**
 *
 */
package de.terrestris.shogun2.model.module;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The CoordinateTransform module allows the user to transform map coordinates into
 * different projections.
 *
 * @author Kai Volland
 *
 */
@Entity
@Table
public class CoordinateTransform extends Module {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A list of EPSG-Codes that should be available in the module.
	 */
	@ElementCollection
	@CollectionTable(name = "COORDINATETRANSFORM_EPSG", joinColumns = @JoinColumn(name = "COORDTRANS_ID") )
	@Column(name = "EPSG")
	@OrderColumn(name = "IDX")
	private List<String> epsgCodes = new ArrayList<String>();

	/**
	 * Whether the form should be filled on instantiation or not.
	 */
	private Boolean transformCenterOnRender;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public CoordinateTransform() {
	}

	/**
	 * @return the epsgCodes
	 */
	public List<String> getEpsgCodes() {
		return epsgCodes;
	}

	/**
	 * @param epsgCodes the epsgCodes to set
	 */
	public void setEpsgCodes(List<String> epsgCodes) {
		this.epsgCodes = epsgCodes;
	}

	/**
	 * @return the transformCenterOnRender
	 */
	public Boolean getTransformCenterOnRender() {
		return transformCenterOnRender;
	}

	/**
	 * @param transformCenterOnRender the transformCenterOnRender to set
	 */
	public void setTransformCenterOnRender(Boolean transformCenterOnRender) {
		this.transformCenterOnRender = transformCenterOnRender;
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
		return new HashCodeBuilder(19, 3).
				appendSuper(super.hashCode()).
				append(getEpsgCodes()).
				append(getTransformCenterOnRender()).
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
		if (!(obj instanceof CoordinateTransform))
			return false;
		CoordinateTransform other = (CoordinateTransform) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getEpsgCodes(), other.getEpsgCodes()).
				append(getTransformCenterOnRender(), other.getTransformCenterOnRender()).
				isEquals();
	}

	/**
	 *
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
