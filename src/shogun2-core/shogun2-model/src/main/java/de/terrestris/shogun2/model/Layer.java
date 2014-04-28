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

	private static final long serialVersionUID = -7363857422959263258L;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}

	public Boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	public Boolean getAlwaysInRange() {
		return alwaysInRange;
	}

	public void setAlwaysInRange(Boolean alwaysInRange) {
		this.alwaysInRange = alwaysInRange;
	}

	public Boolean getDisplayInLayerSwitcher() {
		return displayInLayerSwitcher;
	}

	public void setDisplayInLayerSwitcher(Boolean displayInLayerSwitcher) {
		this.displayInLayerSwitcher = displayInLayerSwitcher;
	}

	public Double getOpacity() {
		return opacity;
	}

	public void setOpacity(Double opacity) {
		this.opacity = opacity;
	}

	public String getAttribution() {
		return attribution;
	}

	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	public Integer getGutter() {
		return gutter;
	}

	public void setGutter(Integer gutter) {
		this.gutter = gutter;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getResolutions() {
		return resolutions;
	}

	public void setResolutions(String resolutions) {
		this.resolutions = resolutions;
	}

	public String getScales() {
		return scales;
	}

	public void setScales(String scales) {
		this.scales = scales;
	}

	public String getMaxExtent() {
		return maxExtent;
	}

	public void setMaxExtent(String maxExtent) {
		this.maxExtent = maxExtent;
	}

	public String getMinExtent() {
		return minExtent;
	}

	public void setMinExtent(String minExtent) {
		this.minExtent = minExtent;
	}

	public Double getMaxResolution() {
		return maxResolution;
	}

	public void setMaxResolution(Double maxResolution) {
		this.maxResolution = maxResolution;
	}

	public Double getMinResolution() {
		return minResolution;
	}

	public void setMinResolution(Double minResolution) {
		this.minResolution = minResolution;
	}

	public Double getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(Double maxScale) {
		this.maxScale = maxScale;
	}

	public Double getMinScale() {
		return minScale;
	}

	public void setMinScale(Double minScale) {
		this.minScale = minScale;
	}

	public Integer getNumZoomLevels() {
		return numZoomLevels;
	}

	public void setNumZoomLevels(Integer numZoomLevels) {
		this.numZoomLevels = numZoomLevels;
	}

	public Boolean getDisplayOutsideMaxExtent() {
		return displayOutsideMaxExtent;
	}

	public void setDisplayOutsideMaxExtent(Boolean displayOutsideMaxExtent) {
		this.displayOutsideMaxExtent = displayOutsideMaxExtent;
	}

	public Boolean getIsBaseLayer() {
		return isBaseLayer;
	}

	public void setIsBaseLayer(Boolean isBaseLayer) {
		this.isBaseLayer = isBaseLayer;
	}

	public Boolean getAlpha() {
		return alpha;
	}

	public void setAlpha(Boolean alpha) {
		this.alpha = alpha;
	}
}
