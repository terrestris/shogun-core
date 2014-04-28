package de.terrestris.shogun2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class represents a WMS layer. The model is mainly based on the
 * OpenLayers.Layer.WMS-model (which inherits from OpenLayers.Layer.Grid):
 * http://dev.openlayers.org/docs/files/OpenLayers/Layer/WMS-js.html
 * 
 * @author Nils Bühner
 * 
 */
@Entity
@Table
public class WmsLayer extends Layer {

	private static final long serialVersionUID = 5673099504395691524L;

	/**
	 * The (base) URL of the WMS Service. This field must not be null.
	 */
	@Column(nullable = false)
	private String url;

	/**
	 * A comma-separated list with layer-names. This field must not be null.
	 */
	@Column(nullable = false)
	private String layers;

	/**
	 * Whether or not the map-tiles of this layer are transparent or not.
	 * Defaults to true. If set to true (which is default in SHOGun2),
	 * OpenLayers will apply some magic: The default {@link #format} changes
	 * from image/jpeg to image/png, and the layer is not configured as
	 * baseLayer.
	 */
	@Column
	private Boolean transparent = true;

	/**
	 * If set to true, the layer (on the map) will be requested in a single
	 * tile. Defaults to false.
	 */
	@Column
	private Boolean singleTile = false;

	/**
	 * Used only when {@link #singleTile} is true. This specifies the ratio of
	 * the size of the single tile to the size of the map. Default value is 1.5.
	 */
	@Column
	private Double ratio = 1.5;

	/**
	 * The image format in which the tiles are requested, e.g. "image/png".
	 */
	@Column
	private String format;

	/**
	 * 
	 */
	@Column
	private String transitionEffect;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public WmsLayer() {
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the layers
	 */
	public String getLayers() {
		return layers;
	}

	/**
	 * @param layers
	 *            the layers to set
	 */
	public void setLayers(String layers) {
		this.layers = layers;
	}

	/**
	 * @return the transparent
	 */
	public Boolean getTransparent() {
		return transparent;
	}

	/**
	 * @param transparent
	 *            the transparent to set
	 */
	public void setTransparent(Boolean transparent) {
		this.transparent = transparent;
	}

	/**
	 * @return the singleTile
	 */
	public Boolean getSingleTile() {
		return singleTile;
	}

	/**
	 * @param singleTile
	 *            the singleTile to set
	 */
	public void setSingleTile(Boolean singleTile) {
		this.singleTile = singleTile;
	}

	/**
	 * @return the ratio
	 */
	public Double getRatio() {
		return ratio;
	}

	/**
	 * @param ratio
	 *            the ratio to set
	 */
	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the transitionEffect
	 */
	public String getTransitionEffect() {
		return transitionEffect;
	}

	/**
	 * @param transitionEffect
	 *            the transitionEffect to set
	 */
	public void setTransitionEffect(String transitionEffect) {
		this.transitionEffect = transitionEffect;
	}

}
