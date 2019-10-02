package de.terrestris.shoguncore.model.layer.source;

import de.terrestris.shoguncore.model.layer.util.Extent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a layer source for tile data with
 * URLs in a set XYZ format that are defined in a URL template
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 */
@Table
@Entity
@Cacheable
public class XyzLayerDataSource extends LayerDataSource {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "CENTER_X")),
        @AttributeOverride(name = "y", column = @Column(name = "CENTER_Y"))
    })
    private Point2D.Double center;

    @OneToOne
    private Extent extent;

    @ElementCollection
    @CollectionTable(
        name = "XYZLAYERDATASRC_RESOLUTION",
        joinColumns = @JoinColumn(name = "XYZLAYERDATASRC_ID"))
    @Column(name = "RESOLUTION")
    @OrderColumn(name = "IDX")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private List<Double> resolutions = new ArrayList<Double>();

    private Integer tileSize;

    /**
     *
     */
    public XyzLayerDataSource() {
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
    public XyzLayerDataSource(String name, String type, String url, String format, Double center, Extent extent,
                              List<Double> resolutions, Integer tileSize) {
        super(name, type, url, format);
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
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
     */
    @Override
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
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XyzLayerDataSource)) {
            return false;
        }
        XyzLayerDataSource other = (XyzLayerDataSource) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getCenter(), other.getCenter()).
            append(getExtent(), other.getExtent()).
            append(getResolutions(), other.getResolutions()).
            append(getTileSize(), other.getTileSize()).
            isEquals();
    }

}
