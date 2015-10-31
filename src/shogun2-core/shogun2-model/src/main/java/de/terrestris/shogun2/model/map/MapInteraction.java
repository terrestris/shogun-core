package de.terrestris.shogun2.model.map;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.PersistentObject;

/**
 *
 * This class represents an
 * <a href="http://openlayers.org/en/master/apidoc/ol.interaction">OpenLayers 3 interaction</a>
 * which can be included in a map.
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class MapInteraction extends PersistentObject{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * String which represents the OL3 class name of the interaction, e.g.
	 * ol.interaction.DragRotate
	 * ol.interaction.DoubleClickZoom
	 * ol.interaction.DragPan
	 * ol.interaction.PinchRotate
	 * ol.interaction.PinchZoom
	 * ol.interaction.KeyboardPan
	 * ol.interaction.KeyboardZoom
	 * ol.interaction.MouseWheelZoom
	 * ol.interaction.DragZoom
	 */
	private String interactionClassName;

	/**
	 *
	 */
	public MapInteraction() {
		super();
	}

	/**
	 *
	 * @param name_
	 */
	public MapInteraction(String name_) {
		super();
		interactionClassName = name_;
	}

	/**
	 * @return the interactionClassName
	 */
	public String getInteractionClassName() {
		return interactionClassName;
	}

	/**
	 * @param interactionClassName the interactionClassName to set
	 */
	public void setInteractionClassName(String interactionClassName) {
		this.interactionClassName = interactionClassName;
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
		return new HashCodeBuilder(17, 13).
				appendSuper(super.hashCode()).
				append(getInteractionClassName()).
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
		if (!(obj instanceof MapInteraction))
			return false;
		MapInteraction other = (MapInteraction) obj;

		return new EqualsBuilder().
				append(getInteractionClassName(), other.getInteractionClassName()).
				appendSuper(super.equals(other)).
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