package de.terrestris.shoguncore.model.map;

import java.awt.geom.Point2D;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.terrestris.shoguncore.model.PersistentObject;
import de.terrestris.shoguncore.model.layer.util.Extent;

/**
 * The <i>MapConfig</i> is backend representation for an
 * <a href="http://openlayers.org/en/master/apidoc/ol.View.html"> OpenLayers 3 View</a>
 *
 * @author Andre Henn
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 */
@Entity
@Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MapConfig extends PersistentObject {

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
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "CENTER_X")),
        @AttributeOverride(name = "y", column = @Column(name = "CENTER_Y"))
    })
    private Point2D.Double center;

    /**
     *
     */
    @ManyToOne
    private Extent extent;

    /**
     *
     */
    @ElementCollection
    @CollectionTable(
        name = "MAPCONFIG_RESOLUTION",
        joinColumns = @JoinColumn(name = "MAPCONFIG_ID"))
    @Column(name = "RESOLUTION")
    @OrderColumn(name = "IDX")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private List<Double> resolutions;

    /**
     *
     */
    private Integer zoom;

    /**
     *
     */
    private Double resolution;

    /**
     *
     */
    private Double maxResolution;

    /**
     *
     */
    private Double minResolution;

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
    public MapConfig(String name, Point2D.Double center, Extent extent, List<Double> resolutions, Integer zoom,
                     Double maxResolution, Double minResolution, Double rotation, String projection) {
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
    public List<Double> getResolutions() {
        return resolutions;
    }

    /**
     * @param resolutions the resolutions to set
     */
    public void setResolutions(List<Double> resolutions) {
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
     * @return the resolution
     */
    public Double getResolution() {
        return resolution;
    }

    /**
     * @param resolution the resolution to set
     */
    public void setResolution(Double resolution) {
        this.resolution = resolution;
    }

    /**
     * @return the maxResolution
     */
    public Double getMaxResolution() {
        return maxResolution;
    }

    /**
     * @param maxResolution the maxResolution to set
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
     * @param minResolution the minResolution to set
     */
    public void setMinResolution(Double minResolution) {
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
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
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
            append(getResolution()).
            append(getMaxResolution()).
            append(getMinResolution()).
            append(getRotation()).
            append(getProjection()).
            toHashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
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
            append(getResolution(), other.getResolution()).
            append(getMaxResolution(), other.getMaxResolution()).
            append(getMinResolution(), other.getMinResolution()).
            append(getRotation(), other.getRotation()).
            append(getProjection(), other.getProjection()).
            isEquals();
    }

}
