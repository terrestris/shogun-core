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
 * This class represents a data source for a {@link Layer}. As this class only
 * provides basic information, a {@link Layer} would usually use a more concrete
 * version of a {@link LayerDataSource} (i.e. a subclass of this class).
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class LayerDataSource extends PersistentObject {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@Column
	private String name;

	/**
	 *
	 */
	@Column
	private String attribution;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public LayerDataSource() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
		return new HashCodeBuilder(11, 23).appendSuper(super.hashCode())
				.append(getName()).append(getAttribution()).toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LayerDataSource))
			return false;
		LayerDataSource other = (LayerDataSource) obj;

		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(getName(), other.getName())
				.append(getAttribution(), other.getAttribution()).isEquals();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 *      Using Apache Commons String Builder.
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.appendSuper(super.toString()).append("name", getName())
				.toString();
	}

}
