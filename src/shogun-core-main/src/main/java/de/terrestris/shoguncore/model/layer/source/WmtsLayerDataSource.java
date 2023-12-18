package de.terrestris.shoguncore.model.layer.source;

import de.terrestris.shoguncore.model.layer.util.WmtsTileGrid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.util.List;

/**
 * Class representing a layer source for tile data from WMTS servers.
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 */
@Entity
@Table
@Cacheable
public class WmtsLayerDataSource extends LayerDataSource {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TILEGRID_ID")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private WmtsTileGrid tileGrid;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<String> urls;

    private String wmtsLayer;
    private String wmtsStyle;
    private String projection;
    private String matrixSet;
    private String requestEncoding;
    private String capabilitiesUrl;

    /**
     * Default constructor
     */
    public WmtsLayerDataSource() {
        super();
    }

    /**
     * @param tileGrid
     * @param wmtsLayer
     * @param wmtsStyle
     * @param projection
     * @param matrixSet
     * @param requestEncoding
     * @param urls
     */
    public WmtsLayerDataSource(WmtsTileGrid tileGrid, String wmtsLayer, String wmtsStyle, String projection,
       String matrixSet, String requestEncoding, List<String> urls, String capabilitiesUrl) {
        this();
        this.tileGrid = tileGrid;
        this.wmtsLayer = wmtsLayer;
        this.wmtsStyle = wmtsStyle;
        this.projection = projection;
        this.matrixSet = matrixSet;
        this.requestEncoding = requestEncoding;
        this.urls = urls;
        this.capabilitiesUrl = capabilitiesUrl;
    }

    /**
     * @return the {@link WmtsTileGrid}
     */
    public WmtsTileGrid getTileGrid() {
        return tileGrid;
    }

    /**
     * @param tileGrid WmtsTileGrid to set
     */
    public void setTileGrid(WmtsTileGrid tileGrid) {
        this.tileGrid = tileGrid;
    }

    /**
     * @return The layer name of WMTS layer
     */
    public String getWmtsLayer() {
        return wmtsLayer;
    }

    /**
     * @param wmtsLayer The layer name to set
     */
    public void setWmtsLayer(String wmtsLayer) {
        this.wmtsLayer = wmtsLayer;
    }

    /**
     * @return The style name to set
     */
    public String getWmtsStyle() {
        return wmtsStyle;
    }

    /**
     * @param wmtsStyle The style name to set
     */
    public void setWmtsStyle(String wmtsStyle) {
        this.wmtsStyle = wmtsStyle;
    }

    /**
     * @return The projection
     */
    public String getProjection() {
        return projection;
    }

    /**
     * @param projection The projection code to set
     */
    public void setProjection(String projection) {
        this.projection = projection;
    }

    /**
     * @return matrix set to use
     */
    public String getMatrixSet() {
        return matrixSet;
    }

    /**
     * @param matrixSet The matrix set to set
     */
    public void setMatrixSet(String matrixSet) {
        this.matrixSet = matrixSet;
    }

    /**
     * @return the request encoding
     */
    public String getRequestEncoding() {
        return requestEncoding;
    }

    /**
     * @param requestEncoding the request encoding to set
     */
    public void setRequestEncoding(String requestEncoding) {
        this.requestEncoding = requestEncoding;
    }

    /**
     * @return The urls as {@link List} of {@link String}
     */
    public List<String> getUrls() {
        return urls;
    }

    /**
     * @param urls The urls to set
     */
    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    /**
     *
     * @return The capabilitiesUrl
     */
    public String getCapabilitiesUrl() {
        return capabilitiesUrl;
    }

    /**
     *
     * @param capabilitiesUrl The capabilitiesUrl to set
     */
    public void setCapabilitiesUrl(String capabilitiesUrl) {
        this.capabilitiesUrl = capabilitiesUrl;
    }

    /**
     * @see java.lang.Object#hashCode()
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
     */
    public int hashCode() {
        // two randomly chosen prime numbers
        return new HashCodeBuilder(11, 19).
            appendSuper(super.hashCode()).
            append(getRequestEncoding()).
            append(getWmtsLayer()).
            append(getMatrixSet()).
            append(getProjection()).
            append(getWmtsStyle()).
            append(getTileGrid()).
            append(getUrls()).
            append(getCapabilitiesUrl()).
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
    public boolean equals(Object obj) {
        if (!(obj instanceof WmtsLayerDataSource)) {
            return false;
        }
        WmtsLayerDataSource other = (WmtsLayerDataSource) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getRequestEncoding(), other.getRequestEncoding()).
            append(getWmtsLayer(), other.getWmtsLayer()).
            append(getMatrixSet(), other.getMatrixSet()).
            append(getProjection(), other.getProjection()).
            append(getWmtsStyle(), other.getWmtsStyle()).
            append(getTileGrid(), other.getTileGrid()).
            append(getUrls(), other.getUrls()).
            append(getCapabilitiesUrl(), other.getCapabilitiesUrl()).
            isEquals();
    }

}
