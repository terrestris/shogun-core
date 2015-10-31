/**
 * 
 */
package de.terrestris.shogun2.model.layer.util;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * 
 * Class representing a WMS tile grid
 * 
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class WMSTileGrid extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The tile grid origin, i.e. where the x and y axes meet ([z, 0, 0]).
	 * Tile coordinates increase left to right and upwards.
	 * If not specified, extent or origins must be provided.
	 */
	private Point2D.Double tileGridOrigin;
	
	/**
	 * Extent for the tile grid. No tiles outside this extent will be requested 
	 * by ol.source.Tile sources. When no origin or origins are configured,
	 * the origin will be set to the top-left corner of the extent.
     * origin
	 */
	private Extent tileGridExtent;
		
	/**
	 * default value: 256
	 */
	private Integer tileSize;

	/**
	 * 
	 */
	public WMSTileGrid() {
		super();
		tileSize = new Integer(256);
	}

	/**
	 * @param tileGridOrigin
	 * @param tileGridExtent
	 * @param tileSize
	 */
	public WMSTileGrid(Double tileGridOrigin, Extent tileGridExtent, Integer tileSize) {
		super();
		this.tileGridOrigin = tileGridOrigin;
		this.tileGridExtent = tileGridExtent;
		this.tileSize = tileSize;
	}

	/**
	 * @return the tileGridOrigin
	 */
	public Point2D.Double getTileGridOrigin() {
		return tileGridOrigin;
	}

	/**
	 * @param tileGridOrigin the tileGridOrigin to set
	 */
	public void setTileGridOrigin(Point2D.Double tileGridOrigin) {
		this.tileGridOrigin = tileGridOrigin;
	}

	/**
	 * @return the tileGridExtent
	 */
	public Extent getTileGridExtent() {
		return tileGridExtent;
	}

	/**
	 * @param tileGridExtent the tileGridExtent to set
	 */
	public void setTileGridExtent(Extent tileGridExtent) {
		this.tileGridExtent = tileGridExtent;
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
		return new HashCodeBuilder(43, 13).
				appendSuper(super.hashCode()).
				append(getTileSize()).
				append(getTileGridOrigin()).
				append(getTileGridExtent()).
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
		if (!(obj instanceof WMSTileGrid))
			return false;
		WMSTileGrid other = (WMSTileGrid) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getTileGridExtent(), other.getTileGridExtent()).
				append(getTileGridOrigin(), other.getTileGridOrigin()).
				append(getTileSize(), other.getTileSize()).
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
