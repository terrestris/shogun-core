package de.terrestris.shoguncore.model.layer.source;

import de.terrestris.shoguncore.model.layer.util.TileGrid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;

/**
 * Data source of layers for tile data from WMS servers.
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 */
@Table
@Entity
@Cacheable
public class TileWmsLayerDataSource extends ImageWmsLayerDataSource {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private TileGrid tileGrid;

    /**
     * Whether to request the layer with TILED=true.
     */
    @Column(name = "REQUEST_WITH_TILED")
    private Boolean requestWithTiled = Boolean.TRUE;

    /**
     * default constructor
     */
    public TileWmsLayerDataSource() {
        super();
    }

    /**
     * @param name
     * @param type
     * @param url
     * @param width
     * @param height
     * @param version
     * @param layerNames
     * @param layerStyles
     * @param tileGrid
     */
    public TileWmsLayerDataSource(String name, String type, String url, String format, int width,
                                  int height, String version, String layerNames, String layerStyles,
                                  TileGrid tileGrid) {
        super(name, type, url, format, width, height, version, layerNames, layerStyles);
        this.tileGrid = tileGrid;
    }

    /**
     * @return the tileGrid
     */
    public TileGrid getTileGrid() {
        return tileGrid;
    }

    /**
     * @param tileGrid the tileGrid to set
     */
    public void setTileGrid(TileGrid tileGrid) {
        this.tileGrid = tileGrid;
    }

    /**
     * @return the requestWithTiled
     */
    public Boolean getRequestWithTiled() {
        return requestWithTiled;
    }

    /**
     * @param requestWithTiled the requestWithTiled to set
     */
    public void setRequestWithTiled(Boolean requestWithTiled) {
        this.requestWithTiled = requestWithTiled;
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
        return new HashCodeBuilder(47, 13).
            appendSuper(super.hashCode()).
            append(getTileGrid()).
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
        if (!(obj instanceof TileWmsLayerDataSource)) {
            return false;
        }
        TileWmsLayerDataSource other = (TileWmsLayerDataSource) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getTileGrid(), other.getTileGrid()).
            isEquals();
    }

}
