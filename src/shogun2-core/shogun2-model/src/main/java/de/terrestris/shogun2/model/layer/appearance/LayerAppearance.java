package de.terrestris.shogun2.model.layer.appearance;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.layer.Layer;
import de.terrestris.shogun2.model.layer.util.Resolution;

/**
 *
 * This class holds the appearance properties of a layer {@link Layer} Object
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class LayerAppearance extends PersistentObject{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private String attribution;

	/**
	 *
	 */
	private String name;

	/**
	 *
	 */
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	// The maxResolution will be serialized (JSON)
	// as the simple resolution value
	@JsonIdentityInfo(
			generator = ObjectIdGenerators.PropertyGenerator.class,
			property = "resolution"
	)
	@JsonIdentityReference(alwaysAsId = true)
	private Resolution maxResolution;

	/**
	 *
	 */
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	// The maxResolution will be serialized (JSON)
	// as the simple resolution value
	@JsonIdentityInfo(
			generator = ObjectIdGenerators.PropertyGenerator.class,
			property = "resolution"
	)
	@JsonIdentityReference(alwaysAsId = true)
	private Resolution minResolution;

	/**
	 *
	 */
	private Double opacity;

	/**
	 *
	 */
	private Boolean visible;

	/**
	 *
	 */
	public LayerAppearance() {
		super();
	}

	/**
	 * @param type
	 * @param attribution
	 * @param name
	 * @param maxResolution
	 * @param minResolution
	 * @param opacity
	 * @param visible
	 */
	public LayerAppearance(String attribution, String name, Resolution maxResolution,
			Resolution minResolution, Double opacity, Boolean visible) {
		super();
		this.attribution = attribution;
		this.name = name;
		this.maxResolution = maxResolution;
		this.minResolution = minResolution;
		this.opacity = opacity;
		this.visible = visible;
	}

	/**
	 * @return the attribution
	 */
	public String getAttribution() {
		return attribution;
	}

	/**
	 * @param attribution the attribution to set
	 */
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the maxResolution
	 */
	public Resolution getMaxResolution() {
		return maxResolution;
	}

	/**
	 * @param maxResolution the maxResolution to set
	 */
	public void setMaxResolution(Resolution maxResolution) {
		this.maxResolution = maxResolution;
	}

	/**
	 * @return the minResolution
	 */
	public Resolution getMinResolution() {
		return minResolution;
	}

	/**
	 * @param minResolution the minResolution to set
	 */
	public void setMinResolution(Resolution minResolution) {
		this.minResolution = minResolution;
	}

	/**
	 * @return the opacity
	 */
	public Double getOpacity() {
		return opacity;
	}

	/**
	 * @param opacity the opacity to set
	 */
	public void setOpacity(Double opacity) {
		this.opacity = opacity;
	}

	/**
	 * @return the visible
	 */
	public Boolean getVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(31, 13).
				appendSuper(super.hashCode()).
				append(getAttribution()).
				append(getName()).
				append(getMaxResolution()).
				append(getMinResolution()).
				append(getOpacity()).
				append(getVisible()).
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
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LayerAppearance))
			return false;
		LayerAppearance other = (LayerAppearance) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getAttribution(), other.getAttribution()).
				append(getName(), other.getName()).
				append(getMaxResolution(), other.getMaxResolution()).
				append(getMinResolution(), other.getMinResolution()).
				append(getOpacity(), other.getOpacity()).
				append(getVisible(), other.getVisible()).
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
