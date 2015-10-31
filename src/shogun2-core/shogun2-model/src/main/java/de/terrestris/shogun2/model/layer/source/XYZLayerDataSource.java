package de.terrestris.shogun2.model.layer.source;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.layer.util.Extent;
import de.terrestris.shogun2.model.layer.util.Resolution;

/**
 * 
 * Class representing a layer source for tile data with 
 * URLs in a set XYZ format that are defined in a URL template
 * 
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Table
@Entity
public class XYZLayerDataSource extends LayerDataSource {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Point2D.Double center;
		
	@OneToOne
	private Extent extent;
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "LAYERDATASOURCE_RESOLUTIONS")
	private List<Resolution> resolutions = new ArrayList<Resolution>();
	
	private Integer tileSize;

	/**
	 * 
	 */
	public XYZLayerDataSource() {
		super();
	}
	
	/**
	 * @param name
	 * @param type
	 * @param url
	 * @param center
	 * @param extent
	 * @param resolutions
	 * @param tileSize
	 */
	public XYZLayerDataSource(String name, String type, String url, Double center, Extent extent,
			List<Resolution> resolutions, Integer tileSize) {
		super(name, type, url);
		this.center = center;
		this.extent = extent;
		this.resolutions = resolutions;
		this.tileSize = tileSize;
	}

	/**
	 * @return the center
	 */
	public Point2D.Double getCenter() {
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
	 * @return the tileSize
	 */
	public Integer getTileSize() {
		return tileSize;
	}

	/**
	 * @param tileSize the tileSize to set
	 */
	public void setTileSize(Integer tileSize) {
		this.tileSize = tileSize;
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
		return new HashCodeBuilder(59, 13).
				appendSuper(super.hashCode()).
				append(getCenter()).
				append(getExtent()).
				append(getResolutions()).
				append(getTileSize()).
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
		if (!(obj instanceof XYZLayerDataSource))
			return false;
		XYZLayerDataSource other = (XYZLayerDataSource) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getCenter(), other.getCenter()).
				append(getExtent(), other.getExtent()).
				append(getResolutions(), other.getResolutions()).
				append(getTileSize(), other.getTileSize()).
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
