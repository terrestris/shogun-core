/**
 * 
 */
package de.terrestris.shogun2.model.layout;

import java.awt.Point;
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

import de.terrestris.shogun2.model.module.CompositeModule;

/**
 * This class is the representation of an absolute layout, where components are
 * anchored in absolute positions, which are stored in the {@link #coords}
 * property.
 * 
 * The order of the {@link #coords} should match the order of the corresponding
 * {@link CompositeModule#getSubModules()}.
 * 
 * @author Nils Bühner
 *
 */
@Table
@Entity
public class AbsoluteLayout extends Layout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public AbsoluteLayout() {
		this.setType("absolute");
	}

	/**
	 * 
	 */
	@ElementCollection
	@CollectionTable(name = "ABSOLUTELAYOUT_COORDS", joinColumns = @JoinColumn(name = "LAYOUT_ID") )
	@Column(name = "COORD")
	@OrderColumn(name = "IDX")
	private List<Point> coords = new ArrayList<Point>();

	/**
	 * @return the coords
	 */
	public List<Point> getCoords() {
		return coords;
	}

	/**
	 * @param coords
	 *            the coords to set
	 */
	public void setCoords(List<Point> coords) {
		this.coords = coords;
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
		return new HashCodeBuilder(11, 13).appendSuper(super.hashCode()).append(getCoords()).toHashCode();
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
		if (!(obj instanceof AbsoluteLayout))
			return false;
		AbsoluteLayout other = (AbsoluteLayout) obj;

		return new EqualsBuilder().appendSuper(super.equals(other)).append(getCoords(), other.getCoords()).isEquals();
	}

	/**
	 *
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
