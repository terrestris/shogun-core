/**
 *
 */
package de.terrestris.shogun2.model.layer.util;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.PersistentObject;

/**
 *
 * Util class representing the extent of a layer or a map.
 * The extent is modellesd by the lower left and the upper
 * right point of the bounding rectangle
 *
 * |--------o
 * |        |
 * o--------|
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class Extent extends PersistentObject {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private Point2D.Double lowerLeft;
	private Point2D.Double upperRight;

	/**
	 *
	 */
	public Extent() {
		super();
	}

	/**
	 * @param lowerLeft
	 * @param upperRight
	 */
	public Extent(Double lowerLeft, Double upperRight) {
		super();
		this.lowerLeft = lowerLeft;
		this.upperRight = upperRight;
	}

	/**
	 * @return the lowerLeft
	 */
	public Point2D.Double getLowerLeft() {
		return lowerLeft;
	}

	/**
	 * @param lowerLeft the lowerLeft to set
	 */
	public void setLowerLeft(Point2D.Double lowerLeft) {
		this.lowerLeft = lowerLeft;
	}

	/**
	 * @return the upperRight
	 */
	public Point2D.Double getUpperRight() {
		return upperRight;
	}

	/**
	 * @param upperRight the upperRight to set
	 */
	public void setUpperRight(Point2D.Double upperRight) {
		this.upperRight = upperRight;
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
		return new HashCodeBuilder(61 ,13).
				appendSuper(super.hashCode()).
				append(getLowerLeft()).
				append(getUpperRight()).
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
		if (!(obj instanceof Extent))
			return false;
		Extent other = (Extent) obj;

		return new EqualsBuilder().
				append(getLowerLeft(), other.getLowerLeft()).
				append(getUpperRight(), other.getUpperRight()).
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
