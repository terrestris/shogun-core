/**
 *
 */
package de.terrestris.shogun2.model.module;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
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

import de.terrestris.shogun2.model.layer.Layer;

/**
 *
 * Class represents a GeoExt.component.OverviewMap, that displays an overview
 * map of a parent map.
 *
 * @author Andre Henn
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
@Table
@Entity
public class OverviewMap extends Module {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private Integer magnification;

	/**
	 * The layers used within this OverviewMap.
	 */
	@ElementCollection
	@JoinTable(
			name = "OVERVIEWMAP_LAYERS",
			joinColumns = { @JoinColumn(name = "OVERVIEWMAP_ID") },
			inverseJoinColumns = { @JoinColumn(name = "LAYER_ID") }
	)
	@OrderColumn(name = "IDX")
	private List<Layer> overviewMapLayers = new ArrayList<Layer>();

	/**
	 *
	 */
	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	@JsonIdentityInfo(
			generator = ObjectIdGenerators.PropertyGenerator.class,
			property = "name"
	)
	@JsonIdentityReference(alwaysAsId = true)
	private Map parentMapModule;

	/**
	 *
	 */
	public OverviewMap() {
		super();
	}

	/**
	 * @param magnification
	 */
	public OverviewMap(Integer magnification) {
		super();
		this.magnification = magnification;
	}

	/**
	 * @return the magnification
	 */
	public Integer getMagnification() {
		return magnification;
	}

	/**
	 * @param magnification the magnification to set
	 */
	public void setMagnification(Integer magnification) {
		this.magnification = magnification;
	}

	/**
	 * @return the overviewMapLayers
	 */
	public List<Layer> getOverviewMapLayers() {
		return overviewMapLayers;
	}

	/**
	 * @param overviewMapLayers the overviewMapLayers to set
	 */
	public void setOverviewMapLayers(List<Layer> overviewMapLayers) {
		this.overviewMapLayers = overviewMapLayers;
	}

	/**
	 * @return the parentMapModule
	 */
	public Map getParentMapModule() {
		return parentMapModule;
	}

	/**
	 * @param parentMapModule the parentMapModule to set
	 */
	public void setParentMapModule(Map parentMapModule) {
		this.parentMapModule = parentMapModule;
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
		return new HashCodeBuilder(47, 13).
				appendSuper(super.hashCode()).
				append(getMagnification()).
				append(getOverviewMapLayers()).
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
		if (!(obj instanceof OverviewMap))
			return false;
		OverviewMap other = (OverviewMap) obj;

		return new EqualsBuilder().
				append(getMagnification(), other.getMagnification()).
				append(getParentMapModule(), other.getParentMapModule()).
				append(getOverviewMapLayers(), other.getOverviewMapLayers()).
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
