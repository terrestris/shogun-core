/**
 *
 */
package de.terrestris.shogun2.model.module;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import de.terrestris.shogun2.model.layer.Layer;
import de.terrestris.shogun2.model.map.MapConfig;

/**
 *
 * Class represents a
 * <a href="https://github.com/geoext/geoext3/blob/master/src/component/Map.js">map component</a>
 *
 * @author Andre Henn
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class Map extends Module {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The MapConfig used by this Map. A MapConfig can be used by several maps
	 * or overview maps.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	private MapConfig mapConfig;

	/**
	 * The layers used within this Map.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "MAP_LAYERS",
			joinColumns = { @JoinColumn(name = "MAP_ID") },
			inverseJoinColumns = { @JoinColumn(name = "LAYER_ID") }
	)
	@OrderColumn(name = "INDEX")
	@Cascade(CascadeType.SAVE_UPDATE)
	private List<Layer> mapLayers = new ArrayList<Layer>();

	/**
	 * default constructor
	 */
	public Map() {
		super();
	}

	/**
	 * @param name
	 * @param magnific
	 * @param mapConfig
	 * @param mapLayers
	 */
	public Map(String name, MapConfig mapConfig, List<Layer> mapLayers) {
		super();
		this.mapConfig = mapConfig;
		this.mapLayers = mapLayers;
	}

	/**
	 * @return the mapConfig
	 */
	public MapConfig getMapConfig() {
		return mapConfig;
	}

	/**
	 * @param mapConfig the mapConfig to set
	 */
	public void setMapConfig(MapConfig mapConfig) {
		this.mapConfig = mapConfig;
	}

	/**
	 * @return the mapLayers
	 */
	public List<Layer> getMapLayers() {
		return mapLayers;
	}

	/**
	 * @param mapLayers the mapLayers to set
	 */
	public void setMapLayers(List<Layer> mapLayers) {
		this.mapLayers = mapLayers;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 *
	 *	  According to
	 *	  http://stackoverflow.com/questions/27581/overriding-equals
	 *	  -and-hashcode-in-java it is recommended only to use getter-methods
	 *	  when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(47, 11).
				appendSuper(super.hashCode()).
				append(getName()).
				append(getMapLayers()).
				append(getMapConfig()).
				toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 *	  According to
	 *	  http://stackoverflow.com/questions/27581/overriding-equals
	 *	  -and-hashcode-in-java it is recommended only to use getter-methods
	 *	  when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Map))
			return false;
		Map other = (Map) obj;

		return new EqualsBuilder().
				append(getName(), other.getName()).
				append(getMapConfig(), other.getMapConfig()).
				append(getMapLayers(), other.getMapLayers()).
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
