package de.terrestris.shoguncore.model.layer.source;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Class representing a layer data source for WFS layers
 *
 * @author Johannes Weskamm
 */
@Entity
@Table
@Cacheable
public class WfsLayerDataSource extends LayerDataSource {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @Column
    private String version;

    /**
     * used for versions later than 1.1.0
     */
    @Column(length = 2048)
    private String typeName;

    /**
     * used for versions 1.1.0 and earlier
     */
    @Column(length = 2048)
    private String typeNames;

    /**
     *
     */
    public WfsLayerDataSource() {
        super();
    }

    /**
     * @param name        Name of datasource
     * @param type        Type of datasource
     * @param url         URL of datasource
     * @param format      The format
     * @param version     WFS version
     * @param typeName    Thr type name
     * @param typeNames   Thr type names
     */
    public WfsLayerDataSource(String name, String type, String url, String format, String version, String typeName, String typeNames) {
        super(name, type, url, format);
        this.version = version;
        this.typeName = typeName;
        this.typeNames = typeNames;
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
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the typeNames
     */
    public String getTypeNames() {
        return typeNames;
    }

    /**
     * @param typeNames the typeNames to set
     */
    public void setTypeNames(String typeNames) {
        this.typeNames = typeNames;
    }

    @Override
    public int hashCode() {
        // two randomly chosen prime numbers
        return new HashCodeBuilder(67, 59).
            appendSuper(super.hashCode()).
            append(getVersion()).
            append(getTypeName()).
            append(getTypeNames()).
            toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WfsLayerDataSource)) {
            return false;
        }
        WfsLayerDataSource other = (WfsLayerDataSource) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getVersion(), other.getVersion()).
            append(getTypeName(), other.getTypeName()).
            append(getTypeNames(), other.getTypeNames()).
            isEquals();
    }

}
