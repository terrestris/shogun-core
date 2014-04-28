package de.terrestris.shogun2.model;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * This class represents a basic (map-)layer. The model is mainly based on the
 * OpenLayers.Layer-model:
 * http://dev.openlayers.org/docs/files/OpenLayers/Layer-js.html
 * 
 * We annotate this abstract class with {@link Entity}. This way we can use
 * {@link Layer} as type of Sets when using "to-many"-relations.
 * 
 * @author Nils Bühner
 * 
 */
@Entity
public abstract class Layer extends PersistentObject {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of the layer. This field must not be null.
	 */
	@Column(nullable = false)
	private String name;

	/**
	 * The projection of the Layer, e.g. "EPSG:3857". If specifying projection,
	 * also set {@link #maxExtent}, {@link #maxResolution} or
	 * {@link #resolutions} as appropriate. When using vector layers with
	 * strategies, layer projection should be set to the projection of the
	 * source data if that is different from the map default. Defaults to null.
	 */
	@Column
	private String projection = null;

	/**
	 * Whether or not the layer is visible in the map. Defaults to true.
	 */
	@Column
	private Boolean visibility = true;

	/**
	 * If a layer’s display should not be scale-based, this should be set to
	 * true. Defaults to false.
	 */
	@Column
	private Boolean alwaysInRange = false;

	/**
	 * Display the layer’s name in the layer switcher. Defaults to true.
	 */
	@Column
	private Boolean displayInLayerSwitcher = true;

	/**
	 * The layer's opacity. Defaults to 1.0.
	 */
	@Column
	private Double opacity = 1.0;

	/**
	 * Attribution information. Defaults to null.
	 */
	@Column
	private String attribution = null;

	/**
	 * Determines the width (in pixels) of the gutter around image tiles to
	 * ignore. Defaults to 0.
	 */
	@Column
	private Integer gutter = 0;

	/**
	 * The layer map units. Defaults to null. Possible values are ‘degrees’ (or
	 * ‘dd’), ‘m’, ‘ft’, ‘km’, ‘mi’, ‘inches’. Normally taken from the
	 * {@link #projection} (by OpenLayers). Only required if both, map and
	 * layers, do not define a projection or if they define a projection which
	 * does not define units. Defaults to null.
	 */
	@Column
	private String units = null;

	/**
	 * A comma-separated list of map resolutions (map units per pixel) in
	 * descending order. Defaults to null.
	 */
	@Column
	private String resolutions = null;

	/**
	 * A comma-separated list of map scales in descending order. Note that these
	 * values only make sense if the display (monitor) resolution of the client
	 * is correctly guessed by whomever is configuring the application. In
	 * addition, the {@link #units} property must also be set. Use
	 * {@link #resolutions} instead wherever possible. Defaults to null.
	 */
	@Column
	private String scales = null;

	/**
	 * The maximum extent for the layer. Defaults to null. The comma-separated
	 * list should consist of four values (left, bottom, right, top). Defaults
	 * to null.
	 */
	@Column
	private String maxExtent = null;

	/**
	 * The minimum extent for the layer. Defaults to null. The comma-separated
	 * list should consist of four values (left, bottom, right, top). Defaults
	 * to null.
	 */
	@Column
	private String minExtent = null;

	/**
	 * The maximum resolution of the layer. Defaults to null.
	 */
	@Column
	private Double maxResolution = null;

	/**
	 * The minimum resolution of the layer. Defaults to null.
	 */
	@Column
	private Double minResolution = null;

	/**
	 * The maximum scale of the layer. Defaults to null.
	 */
	@Column
	private Double maxScale = null;

	/**
	 * The minimum scale of the layer. Defaults to null.
	 */
	@Column
	private Double minScale = null;

	/**
	 * The number of zoom levels of the layer. Defaults to null.
	 */
	@Column
	private Integer numZoomLevels = null;

	/**
	 * Request map tiles that are completely outside of the {@link #maxExtent}
	 * for this layer. Defaults to false.
	 */
	@Column
	private Boolean displayOutsideMaxExtent = false;

	/**
	 * Whether or not the layer is a base layer. Defaults to false.
	 */
	@Column
	private Boolean isBaseLayer = false;

	/**
	 * Whether or not the layer's images have an alpha channel. Defaults to
	 * false.
	 */
	@Column
	private Boolean alpha = false;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public Layer() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the projection
	 */
	public String getProjection() {
		return projection;
	}

	/**
	 * @param projection
	 *            the projection to set
	 */
	public void setProjection(String projection) {
		this.projection = projection;
	}

	/**
	 * @return the visibility
	 */
	public Boolean getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility
	 *            the visibility to set
	 */
	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	/**
	 * @return the alwaysInRange
	 */
	public Boolean getAlwaysInRange() {
		return alwaysInRange;
	}

	/**
	 * @param alwaysInRange
	 *            the alwaysInRange to set
	 */
	public void setAlwaysInRange(Boolean alwaysInRange) {
		this.alwaysInRange = alwaysInRange;
	}

	/**
	 * @return the displayInLayerSwitcher
	 */
	public Boolean getDisplayInLayerSwitcher() {
		return displayInLayerSwitcher;
	}

	/**
	 * @param displayInLayerSwitcher
	 *            the displayInLayerSwitcher to set
	 */
	public void setDisplayInLayerSwitcher(Boolean displayInLayerSwitcher) {
		this.displayInLayerSwitcher = displayInLayerSwitcher;
	}

	/**
	 * @return the opacity
	 */
	public Double getOpacity() {
		return opacity;
	}

	/**
	 * @param opacity
	 *            the opacity to set
	 */
	public void setOpacity(Double opacity) {
		this.opacity = opacity;
	}

	/**
	 * @return the attribution
	 */
	public String getAttribution() {
		return attribution;
	}

	/**
	 * @param attribution
	 *            the attribution to set
	 */
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	/**
	 * @return the gutter
	 */
	public Integer getGutter() {
		return gutter;
	}

	/**
	 * @param gutter
	 *            the gutter to set
	 */
	public void setGutter(Integer gutter) {
		this.gutter = gutter;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @param units
	 *            the units to set
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * @return the resolutions
	 */
	public String getResolutions() {
		return resolutions;
	}

	/**
	 * @param resolutions
	 *            the resolutions to set
	 */
	public void setResolutions(String resolutions) {
		this.resolutions = resolutions;
	}

	/**
	 * @return the scales
	 */
	public String getScales() {
		return scales;
	}

	/**
	 * @param scales
	 *            the scales to set
	 */
	public void setScales(String scales) {
		this.scales = scales;
	}

	/**
	 * @return the maxExtent
	 */
	public String getMaxExtent() {
		return maxExtent;
	}

	/**
	 * @param maxExtent
	 *            the maxExtent to set
	 */
	public void setMaxExtent(String maxExtent) {
		this.maxExtent = maxExtent;
	}

	/**
	 * @return the minExtent
	 */
	public String getMinExtent() {
		return minExtent;
	}

	/**
	 * @param minExtent
	 *            the minExtent to set
	 */
	public void setMinExtent(String minExtent) {
		this.minExtent = minExtent;
	}

	/**
	 * @return the maxResolution
	 */
	public Double getMaxResolution() {
		return maxResolution;
	}

	/**
	 * @param maxResolution
	 *            the maxResolution to set
	 */
	public void setMaxResolution(Double maxResolution) {
		this.maxResolution = maxResolution;
	}

	/**
	 * @return the minResolution
	 */
	public Double getMinResolution() {
		return minResolution;
	}

	/**
	 * @param minResolution
	 *            the minResolution to set
	 */
	public void setMinResolution(Double minResolution) {
		this.minResolution = minResolution;
	}

	/**
	 * @return the maxScale
	 */
	public Double getMaxScale() {
		return maxScale;
	}

	/**
	 * @param maxScale
	 *            the maxScale to set
	 */
	public void setMaxScale(Double maxScale) {
		this.maxScale = maxScale;
	}

	/**
	 * @return the minScale
	 */
	public Double getMinScale() {
		return minScale;
	}

	/**
	 * @param minScale
	 *            the minScale to set
	 */
	public void setMinScale(Double minScale) {
		this.minScale = minScale;
	}

	/**
	 * @return the numZoomLevels
	 */
	public Integer getNumZoomLevels() {
		return numZoomLevels;
	}

	/**
	 * @param numZoomLevels
	 *            the numZoomLevels to set
	 */
	public void setNumZoomLevels(Integer numZoomLevels) {
		this.numZoomLevels = numZoomLevels;
	}

	/**
	 * @return the displayOutsideMaxExtent
	 */
	public Boolean getDisplayOutsideMaxExtent() {
		return displayOutsideMaxExtent;
	}

	/**
	 * @param displayOutsideMaxExtent
	 *            the displayOutsideMaxExtent to set
	 */
	public void setDisplayOutsideMaxExtent(Boolean displayOutsideMaxExtent) {
		this.displayOutsideMaxExtent = displayOutsideMaxExtent;
	}

	/**
	 * @return the isBaseLayer
	 */
	public Boolean getIsBaseLayer() {
		return isBaseLayer;
	}

	/**
	 * @param isBaseLayer
	 *            the isBaseLayer to set
	 */
	public void setIsBaseLayer(Boolean isBaseLayer) {
		this.isBaseLayer = isBaseLayer;
	}

	/**
	 * @return the alpha
	 */
	public Boolean getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha
	 *            the alpha to set
	 */
	public void setAlpha(Boolean alpha) {
		this.alpha = alpha;
	}
}
