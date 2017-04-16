package de.terrestris.shogun2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * This class represents a basic (map-)layer.
 * 
 * We annotate this abstract class with {@link Entity}. This way we can use
 * {@link Layer} as type of Sets when using "to-many"-relations.
 * 
 * @author Nils Bühner
 * 
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Layer extends PersistentObject {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of the layer. This field is mandatory.
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * The projection of the Layer.
	 */
	@Column
	private String projection = null;

	/**
	 * Whether or not the layer is visible in the map. Defaults to true.
	 */
	@Column
	private Boolean visibility = true;

	/**
	 * The layer's opacity. Defaults to 1.0.
	 */
	@Column
	private Double opacity = 1.0;

	/**
	 * Attribution information. Defaults to null.
	 */
	@Column
	private String attribution = null;

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
	 * @return the projection
	 */
	public String getProjection() {
		return projection;
	}

	/**
	 * @param projection
	 *            the projection to set
	 */
	public void setProjection(String projection) {
		this.projection = projection;
	}

	/**
	 * @return the visibility
	 */
	public Boolean getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility
	 *            the visibility to set
	 */
	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	/**
	 * @return the opacity
	 */
	public Double getOpacity() {
		return opacity;
	}

	/**
	 * @param opacity
	 *            the opacity to set
	 */
	public void setOpacity(Double opacity) {
		this.opacity = opacity;
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

}
