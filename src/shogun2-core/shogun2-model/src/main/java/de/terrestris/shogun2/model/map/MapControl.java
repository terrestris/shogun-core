package de.terrestris.shogun2.model.map;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.converter.PropertyValueConverter;
import de.terrestris.shogun2.model.PersistentObject;

/**
 * This class represents an
 * <a href="http://openlayers.org/en/master/apidoc/ol.control.Control.html">OpenLayers 3 control</a>
 * which can be included in a map.
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class MapControl extends PersistentObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * String which represents the OL3 class name of the interaction, e.g.
	 * ol.control.Zoom
	 * ol.control.Rotate
	 * ol.control.Attribution
	 * ...
	 */
	private String mapControlName;

	/**
	 *
	 */
	@ElementCollection
	@MapKeyColumn(name = "PROPERTY")
	@Column(name = "VALUE")
	@CollectionTable(
		name = "MAPCONTROLS_PROPERTIES",
		joinColumns = @JoinColumn(name = "MAPCONTROL_ID")
	)
	@Convert(
		converter = PropertyValueConverter.class,
		attributeName = "value"
	)
	private Map<String, Object> mapControlProperties = new HashMap<String, Object>();

	/**
	 *
	 */
	public MapControl() {
		super();
	}

	public MapControl(String name_){
		super();
		this.mapControlName = name_;
	}

	/**
	 * @return the mapControlName
	 */
	public String getMapControlName() {
		return mapControlName;
	}

	/**
	 * @param mapControlName the mapControlName to set
	 */
	public void setMapControlName(String mapControlName) {
		this.mapControlName = mapControlName;
	}

	/**
	 * @return the mapControlProperties
	 */
	public Map<String, Object> getMapControlProperties() {
		return mapControlProperties;
	}

	/**
	 * @param mapControlProperties the mapControlProperties to set
	 */
	public void setMapControlProperties(Map<String, Object> mapControlProperties) {
		this.mapControlProperties = mapControlProperties;
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
		return new HashCodeBuilder(7, 13)
				.appendSuper(super.hashCode())
				.append(getMapControlName())
				.append(getMapControlProperties())
				.toHashCode();
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
		if (!(obj instanceof MapControl))
			return false;
		MapControl other = (MapControl) obj;

		return new EqualsBuilder().
				append(getMapControlName(), other.getMapControlName()).
				append(getMapControlProperties(), other.getMapControlProperties()).
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
