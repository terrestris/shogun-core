package de.terrestris.shogun2.model.map;

import java.awt.geom.Point2D;
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

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.layer.util.Extent;
import de.terrestris.shogun2.model.layer.util.Resolution;

/**
 * The <i>MapConfig</i> is backend representation for an
 * <a href="http://openlayers.org/en/master/apidoc/ol.View.html"> OpenLayers 3 View</a>
 *
 * @author Andre Henn
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class MapConfig extends PersistentObject{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private String name;

	/**
	 *
	 */
	private Point2D.Double center;

	/**
	 *
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	private Extent extent;

	/**
	 *
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	@JoinTable(
			name = "MAPCONFIG_RESOLUTION",
			joinColumns = { @JoinColumn(name = "MAPCONFIG_ID") },
			inverseJoinColumns = { @JoinColumn(name = "RESOLUTION_ID") }
	)
	@OrderColumn(name = "INDEX")
	private List<Resolution> resolutions;

	/**
	 *
	 */
	private Integer zoom;

	/**
	 *
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	private Resolution maxResolution;

	/**
	 *
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade(CascadeType.SAVE_UPDATE)
	private Resolution minResolution;

	/**
	 *
	 */
	private Double rotation;

	/*
	 * use String as datatype since classical EPSG code
	 * as well as OGC URN (urn:x-ogc:def:crs:EPSG:XXXX) should be covered.
	 */
	private String projection;

	/**
	 * default constructor
	 */
	public MapConfig() {
		super();
	}

	/**
	 * @param name
	 * @param center
	 * @param extent
	 * @param resolutions
	 * @param zoom
	 * @param maxResolution
	 * @param minResolution
	 * @param rotation
	 * @param projection
	 */
	public MapConfig(String name, Point2D.Double center, Extent extent, List<Resolution> resolutions, Integer zoom,
			Resolution maxResolution, Resolution minResolution, Double rotation, String projection) {
		super();
		this.name = name;
		this.center = center;
		this.extent = extent;
		this.resolutions = resolutions;
		this.zoom = zoom;
		this.maxResolution = maxResolution;
		this.minResolution = minResolution;
		this.rotation = rotation;
		this.projection = projection;
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
	 * @return the center
	 */
	public Point2D getCenter() {
		return center;
	}

	/**
	 * @param center the center to set
	 */
	public void setCenter(Point2D.Double center) {
		this.center = center;
	}

	/**
	 * @return the extent
	 */
	public Extent getExtent() {
		return extent;
	}

	/**
	 * @param extent the extent to set
	 */
	public void setExtent(Extent extent) {
		this.extent = extent;
	}

	/**
	 * @return the resolutions
	 */
	public List<Resolution> getResolutions() {
		return resolutions;
	}

	/**
	 * @param resolutions the resolutions to set
	 */
	public void setResolutions(List<Resolution> resolutions) {
		this.resolutions = resolutions;
	}

	/**
	 * @return the zoom
	 */
	public Integer getZoom() {
		return zoom;
	}

	/**
	 * @param zoom the zoom to set
	 */
	public void setZoom(Integer zoom) {
		this.zoom = zoom;
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
	 * @return the rotation
	 */
	public Double getRotation() {
		return rotation;
	}

	/**
	 * @param rotation the rotation to set
	 */
	public void setRotation(Double rotation) {
		this.rotation = rotation;
	}

	/**
	 * @return the projection
	 */
	public String getProjection() {
		return projection;
	}

	/**
	 * @param projection the projection to set
	 */
	public void setProjection(String projection) {
		this.projection = projection;
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
		return new HashCodeBuilder(5, 13).
				appendSuper(super.hashCode()).
				append(getName()).
				append(getCenter()).
				append(getExtent()).
				append(getResolutions()).
				append(getZoom()).
				append(getMaxResolution()).
				append(getMinResolution()).
				append(getRotation()).
				append(getProjection()).
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
		if (!(obj instanceof MapConfig))
			return false;
		MapConfig other = (MapConfig) obj;

		return new EqualsBuilder().
				append(getName(), other.getName()).
				append(getCenter(), other.getCenter()).
				append(getExtent(), other.getExtent()).
				append(getResolutions(), other.getResolutions()).
				append(getZoom(), other.getZoom()).
				append(getMaxResolution(), other.getMaxResolution()).
				append(getMinResolution(), other.getMinResolution()).
				append(getRotation(), other.getRotation()).
				append(getProjection(), other.getProjection()).
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
