package de.terrestris.shogun2.model.layer.source;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.layer.util.GeoWebServiceLayerName;
import de.terrestris.shogun2.model.layer.util.GeoWebServiceLayerStyle;
import de.terrestris.shogun2.model.layer.util.WmsTileGrid;

/**
 * Data source of layers for tile data from WMS servers.
 * 
 * @author Andre Henn
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
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "LAYERDATASOURCE_LAYERNAME", joinColumns = { @JoinColumn(name = "DATASOURCE_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "LAYERNAME_ID") })
	private List<GeoWebServiceLayerName> layerNames;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "LAYERDATASOURCE_STYLE", joinColumns = { @JoinColumn(name = "DATASOURCE_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "STYLE_ID") })
	private List<GeoWebServiceLayerStyle> layerStyles;
	
	@OneToOne
	private WmsTileGrid tileGrid;
	
	
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
			List<GeoWebServiceLayerName> layers,
			List<GeoWebServiceLayerStyle> styles, WmsTileGrid tileGrid) {
		super(name, type, url);
		this.width = width;
		this.height = height;
		this.version = version;
		this.layerNames = layers;
		this.layerStyles = styles;
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
	 * @return the layers
	 */
	public List<GeoWebServiceLayerName> getLayers() {
		return layerNames;
	}


	/**
	 * @param layers the layers to set
	 */
	public void setLayers(List<GeoWebServiceLayerName> layers) {
		this.layerNames = layers;
	}


	/**
	 * @return the styles
	 */
	public List<GeoWebServiceLayerStyle> getStyles() {
		return layerStyles;
	}

	/**
	 * @param styles the styles to set
	 */
	public void setStyles(List<GeoWebServiceLayerStyle> styles) {
		this.layerStyles = styles;
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
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(47, 13).
				appendSuper(super.hashCode()).
				append(getWidth()).
				append(getHeight()).
				append(getVersion()).
				append(getLayers()).
				append(getStyles()).
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
	public boolean equals(Object obj) {
		if (!(obj instanceof TileWmsLayerDataSource))
			return false;
		TileWmsLayerDataSource other = (TileWmsLayerDataSource) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getWidth(), other.getWidth()).
				append(getHeight(), other.getHeight()).
				append(getVersion(), other.getVersion()).
				append(getLayers(), other.getLayers()).
				append(getStyles(), other.getStyles()).
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
