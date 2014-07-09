package de.terrestris.shogun2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class represents a basic layout/theme for a {@link Layer} or
 * {@link LayerTreeNode}.
 *
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BaseLayerTheme extends PersistentObject {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@Column
	private double opacity;

	/**
	 *
	 */
	@Column
	private boolean visible;

	/**
	 *
	 */
	@Column
	private String attribution;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public BaseLayerTheme() {
	}

	/**
	 * @return the opacity
	 */
	public double getOpacity() {
		return opacity;
	}

	/**
	 * @param opacity
	 *            the opacity to set
	 */
	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the attribution
	 */
	public String getAttribution() {
		return attribution;
	}

	/**
	 * @param attribution
	 *            the attribution to set
	 */
	public void setAttribution(String attribution) {
		this.attribution = attribution;
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
		return new HashCodeBuilder(13, 37).appendSuper(super.hashCode())
				.append(getOpacity()).append(isVisible())
				.append(getAttribution()).toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BaseLayerTheme))
			return false;
		BaseLayerTheme other = (BaseLayerTheme) obj;

		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(getOpacity(), other.getOpacity())
				.append(isVisible(), other.isVisible())
				.append(getAttribution(), other.getAttribution()).isEquals();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 *      Using Apache Commons String Builder.
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.appendSuper(super.toString()).append("opacity", getOpacity())
				.append("isVisible", isVisible())
				.append("attribution", getAttribution()).toString();
	}

}
