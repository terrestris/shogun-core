package de.terrestris.shogun2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

}
