package de.terrestris.shogun2.model.layer.appearance;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.layer.Layer;

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
	private static final long serialVersionUID = -9007517607427444496L;
	
	private String type;
	private String attribution;
	private String name;
	private Double maxScale;
	private Double minScale;
	private Double opacity;
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
	 * @param maxScale
	 * @param minScale
	 * @param opacity
	 * @param visible
	 */
	public LayerAppearance(String type, String attribution, String name, Double maxScale, Double minScale,
			Double opacity, Boolean visible) {
		super();
		this.type = type;
		this.attribution = attribution;
		this.name = name;
		this.maxScale = maxScale;
		this.minScale = minScale;
		this.opacity = opacity;
		this.visible = visible;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @return the maxScale
	 */
	public Double getMaxScale() {
		return maxScale;
	}

	/**
	 * @param maxScale the maxScale to set
	 */
	public void setMaxScale(Double maxScale) {
		this.maxScale = maxScale;
	}

	/**
	 * @return the minScale
	 */
	public Double getMinScale() {
		return minScale;
	}

	/**
	 * @param minScale the minScale to set
	 */
	public void setMinScale(Double minScale) {
		this.minScale = minScale;
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
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(31, 13).
				appendSuper(super.hashCode()).
				append(getType()).
				append(getAttribution()).
				append(getName()).
				append(getMaxScale()).
				append(getMinScale()).
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
	public boolean equals(Object obj) {
		if (!(obj instanceof LayerAppearance))
			return false;
		LayerAppearance other = (LayerAppearance) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getType(), other.getType()).
				append(getAttribution(), other.getAttribution()).
				append(getName(), other.getName()).
				append(getMaxScale(), other.getMaxScale()).
				append(getMinScale(), other.getMinScale()).
				append(getOpacity(), other.getOpacity()).
				append(getVisible(), other.getVisible()).
				isEquals();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}