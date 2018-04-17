/**
 *
 */
package de.terrestris.shogun2.model.layer.util;

import java.awt.geom.Point2D;
import java.util.List;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * Class representing a WMS tile grid
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 */
@Entity
@Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy = InheritanceType.JOINED)
public class TileGrid extends PersistentObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The tileGrid type. Typically one of `TileGrid` or `WMTS`.
     */
    private String type;

    /**
     * The tile grid origin, i.e. where the x and y axes meet ([z, 0, 0]).
     * Tile coordinates increase left to right and upwards.
     * If not specified, extent or origins must be provided.
     */
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "x", column = @Column(name = "TILEGRIDORIGIN_X")),
        @AttributeOverride(name = "y", column = @Column(name = "TILEGRIDORIGIN_Y"))
    })
    private Point2D.Double tileGridOrigin;

    /**
     * Extent for the tile grid. No tiles outside this extent will be requested
     * by ol.source.Tile sources. When no origin or origins are configured,
     * the origin will be set to the top-left corner of the extent.
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private Extent tileGridExtent;

    /**
     * default value: 256
     */
    private Integer tileSize;

    /**
     * The tileGrid resolutions.
     */
    @ElementCollection
    @CollectionTable(
        name = "TILEGRID_RESOLUTION",
        joinColumns = @JoinColumn(name = "TILEGRID_ID"))
    @Column(name = "RESOLUTION")
    @OrderColumn(name = "IDX")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private List<Double> tileGridResolutions;

    /**
     *
     */
    public TileGrid() {
        super();
        tileSize = new Integer(256);
    }

    /**
     * @param tileGridOrigin
     * @param tileGridExtent
     * @param tileSize
     */
    public TileGrid(Point2D.Double tileGridOrigin, Extent tileGridExtent, Integer tileSize) {
        super();
        this.tileGridOrigin = tileGridOrigin;
        this.tileGridExtent = tileGridExtent;
        this.tileSize = tileSize;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
     * @return the tileGridResolutions
     */
    public List<Double> getTileGridResolutions() {
        return tileGridResolutions;
    }

    /**
     * @param tileGridResolutions the tileGridResolutions to set
     */
    public void setTileGridResolutions(List<Double> tileGridResolutions) {
        this.tileGridResolutions = tileGridResolutions;
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
        return new HashCodeBuilder(43, 13).
            appendSuper(super.hashCode()).
            append(getType()).
            append(getTileGridOrigin()).
            append(getTileGridExtent()).
            append(getTileSize()).
            append(getTileGridResolutions()).
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
        if (!(obj instanceof TileGrid))
            return false;
        TileGrid other = (TileGrid) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getType(), other.getType()).
            append(getTileGridOrigin(), other.getTileGridOrigin()).
            append(getTileGridExtent(), other.getTileGridExtent()).
            append(getTileSize(), other.getTileSize()).
            append(getTileGridResolutions(), other.getTileGridResolutions()).
            isEquals();
    }

}
