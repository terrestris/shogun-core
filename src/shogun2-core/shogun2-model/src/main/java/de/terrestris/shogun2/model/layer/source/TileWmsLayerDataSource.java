package de.terrestris.shogun2.model.layer.source;

import java.util.List;

import javax.persistence.Entity;
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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.terrestris.shogun2.model.layer.util.GeoWebServiceLayerName;
import de.terrestris.shogun2.model.layer.util.GeoWebServiceLayerStyle;
import de.terrestris.shogun2.model.layer.util.WmsTileGrid;

/**
 * Data source of layers for tile data from WMS servers.
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Table
@Entity
public class TileWmsLayerDataSource extends LayerDataSource {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private String version;

	@ManyToMany
	@JoinTable(
		name = "TILEWMSLAYERDATASRC_LAYERNAME",
		joinColumns = { @JoinColumn(name = "TILEWMSLAYERDATASOURCE_ID") },
		inverseJoinColumns = { @JoinColumn(name = "LAYERNAME_ID") }
	)
	@OrderColumn(name = "IDX")
	@Cascade(CascadeType.SAVE_UPDATE)
	// The List of layerNames will be serialized (JSON) as an array of
	// simple layerName string values
	@JsonIdentityInfo(
			generator = ObjectIdGenerators.PropertyGenerator.class,
			property = "layerName"
	)
	@JsonIdentityReference(alwaysAsId = true)
	private List<GeoWebServiceLayerName> layerNames;

	@ManyToMany
	@JoinTable(
		name = "TILEWMSLAYERDATASOURCE_STYLE",
		joinColumns = { @JoinColumn(name = "TILEWMSLAYERDATASOURCE_ID") },
		inverseJoinColumns = { @JoinColumn(name = "STYLE_ID") }
	)
	@OrderColumn(name = "IDX")
	@Cascade(CascadeType.SAVE_UPDATE)
	// The List of layerStyles will be serialized (JSON) as an array of
	// simple layerStyle string values
	@JsonIdentityInfo(
			generator = ObjectIdGenerators.PropertyGenerator.class,
			property = "styleName"
	)
	@JsonIdentityReference(alwaysAsId = true)
	private List<GeoWebServiceLayerStyle> layerStyles;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private WmsTileGrid tileGrid;

	/**
	 * default constructor
	 */
	public TileWmsLayerDataSource(){
		super();
	}

	/**
	 * @param name
	 * @param type
	 * @param url
	 * @param width
	 * @param height
	 * @param version
	 * @param layers
	 * @param styles
	 * @param tileGrid
	 */
	public TileWmsLayerDataSource(String name, String type, String url, int width, int height, String version,
			List<GeoWebServiceLayerName> layerNames,
			List<GeoWebServiceLayerStyle> layerStyles, WmsTileGrid tileGrid) {
		super(name, type, url);
		this.width = width;
		this.height = height;
		this.version = version;
		this.layerNames = layerNames;
		this.layerStyles = layerStyles;
		this.tileGrid = tileGrid;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}


	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}


	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}


	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}


	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	/**
	 * @return the layerNames
	 */
	public List<GeoWebServiceLayerName> getLayerNames() {
		return layerNames;
	}


	/**
	 * @param layerNames the layerNames to set
	 */
	public void setLayerNames(List<GeoWebServiceLayerName> layerNames) {
		this.layerNames = layerNames;
	}

	/**
	 * @return the layerStyles
	 */
	public List<GeoWebServiceLayerStyle> getLayerStyles() {
		return layerStyles;
	}

	/**
	 * @param layerStyles the layerStyles to set
	 */
	public void setLayerStyles(List<GeoWebServiceLayerStyle> layerStyles) {
		this.layerStyles = layerStyles;
	}

	/**
	 * @return the tileGrid
	 */
	public WmsTileGrid getTileGrid() {
		return tileGrid;
	}

	/**
	 * @param tileGrid the tileGrid to set
	 */
	public void setTileGrid(WmsTileGrid tileGrid) {
		this.tileGrid = tileGrid;
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
				append(getWidth()).
				append(getHeight()).
				append(getVersion()).
				append(getLayerNames()).
				append(getLayerStyles()).
				append(getTileGrid()).
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
		if (!(obj instanceof TileWmsLayerDataSource))
			return false;
		TileWmsLayerDataSource other = (TileWmsLayerDataSource) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getWidth(), other.getWidth()).
				append(getHeight(), other.getHeight()).
				append(getVersion(), other.getVersion()).
				append(getLayerNames(), other.getLayerNames()).
				append(getLayerStyles(), other.getLayerStyles()).
				append(getTileGrid(), other.getTileGrid()).
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
