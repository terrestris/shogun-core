package de.terrestris.shogun2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 
 */
@Entity
@Table
public class Layer extends PersistentObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@Column
	private String name;

	/**
	 *
	 */
	@ManyToOne(optional = false)
	private LayerDataSource dataSource;

	/**
	 *
	 */
	@ManyToOne(optional = false)
	private BaseLayerTheme defaultTheme;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public Layer() {
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
	 * @return the dataSource
	 */
	public LayerDataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(LayerDataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the defaultTheme
	 */
	public BaseLayerTheme getDefaultTheme() {
		return defaultTheme;
	}

	/**
	 * @param defaultTheme
	 *            the defaultTheme to set
	 */
	public void setDefaultTheme(BaseLayerTheme defaultTheme) {
		this.defaultTheme = defaultTheme;
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
		return new HashCodeBuilder(17, 29).appendSuper(super.hashCode())
				.append(getName()).append(getDataSource())
				.append(getDefaultTheme()).toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Layer))
			return false;
		Layer other = (Layer) obj;

		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(getName(), other.getName())
				.append(getDataSource(), other.getDataSource())
				.append(getDefaultTheme(), other.getDefaultTheme()).isEquals();
	}

}
