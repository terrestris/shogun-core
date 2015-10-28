/**
 * 
 */
package de.terrestris.shogun2.model.module;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A LayerTree is a simple module, where layers (of a map) are organized in a
 * flexible tree structure.
 * 
 * @author Nils Bühner
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
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public CoordinateTransform() {
	}
	
	/**
	 * A list of EPSG-Codes the should be available in the module.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "CoordinateTransform_EPSG", joinColumns = @JoinColumn(name = "CoordTrans_ID") )
	@Column(name = "EPSG")
	@OrderColumn(name = "INDEX")
	private List<String> epsgCodes = new ArrayList<String>();
	
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
	 * Should the form be filled on instantiation.
	 */
	private Boolean transformCenterOnRender;
	
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
		return new HashCodeBuilder(19, 3).appendSuper(super.hashCode()).toHashCode();
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

		return new EqualsBuilder().appendSuper(super.equals(other)).isEquals();
	}

	/**
	 *
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).appendSuper(super.toString()).toString();
	}

}
