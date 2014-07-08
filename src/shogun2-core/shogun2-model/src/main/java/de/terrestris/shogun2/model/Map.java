package de.terrestris.shogun2.model;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Class providing basic information to represent a map.
 */
@Entity
@Table
public class Map extends Module {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@ElementCollection
	@OrderColumn
	private List<Double> center;

	/**
	 *
	 */
	@ElementCollection
	@OrderColumn
	private List<Double> bbox;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public Map() {
	}

	/**
	 * @return the center
	 */
	public List<Double> getCenter() {
		return center;
	}

	/**
	 * @param center
	 *            the center to set
	 */
	public void setCenter(List<Double> center) {
		this.center = center;
	}

	/**
	 * @return the bbox
	 */
	public List<Double> getBbox() {
		return bbox;
	}

	/**
	 * @param bbox
	 *            the bbox to set
	 */
	public void setBbox(List<Double> bbox) {
		this.bbox = bbox;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 * 
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(13, 41).appendSuper(super.hashCode())
				.append(getName()).append(getBbox()).append(getCenter())
				.toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Map))
			return false;
		Map other = (Map) obj;

		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(getBbox(), other.getBbox())
				.append(getCenter(), other.getCenter()).isEquals();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *      Using Apache Commons String Builder.
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.appendSuper(super.toString()).append("bbox", getBbox())
				.append("center", getCenter()).toString();
	}

}
