package de.terrestris.shoguncore.model.layer.source;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Class representing a layer data source for WMS servers providing single,
 * untiled images.
 *
 * @author Andre Henn
 */
@Entity
@Table
@Cacheable
public class ImageWmsLayerDataSource extends LayerDataSource {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int width;
    private int height;
    private String version;

    /**
     *
     */
    @Column(length = 2048)
    private String layerNames;

    /**
     *
     */
    @Column(length = 2048)
    private String layerStyles;

    @Column
    private Boolean requestableByPath;

    @Column
    private String customRequestPath;

    /**
     *
     */
    public ImageWmsLayerDataSource() {
        super();
    }

    /**
     * @param name    Name of datasource
     * @param type    Type of datasource
     * @param url     URL of datasource
     * @param width   image width
     * @param height  image height
     * @param version WMS version
<<<<<<< HEAD:src/shogun-core-main/src/main/java/de/terrestris/shoguncore/model/layer/source/ImageWmsLayerDataSource.java
     * @param layerNames  List of layer names
=======
     * @param layerNames List of layer names
>>>>>>> add-custom-endpoints:src/shogun2-core/src/main/java/de/terrestris/shogun2/model/layer/source/ImageWmsLayerDataSource.java
     * @param layerStyles  List of layer styles
     */
    public ImageWmsLayerDataSource(String name, String type, String url, int width,
                                   int height, String version, String layerNames, String layerStyles) {
        super(name, type, url);
        this.width = width;
        this.height = height;
        this.version = version;
        this.layerNames = layerNames;
        this.layerStyles = layerStyles;
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
     * @return the layerNames
     */
    public String getLayerNames() {
        return layerNames;
    }


    /**
     * @param layerNames the layerNames to set
     */
    public void setLayerNames(String layerNames) {
        this.layerNames = layerNames;
    }

    /**
     * @return the layerStyles
     */
    public String getLayerStyles() {
        return layerStyles;
    }

    /**
     * @param layerStyles the layerStyles to set
     */
    public void setLayerStyles(String layerStyles) {
        this.layerStyles = layerStyles;
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
        return new HashCodeBuilder(41, 13).
            appendSuper(super.hashCode()).
            append(getWidth()).
            append(getHeight()).
            append(getVersion()).
            append(getLayerNames()).
            append(getLayerStyles()).
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
        if (!(obj instanceof ImageWmsLayerDataSource))
            return false;
        ImageWmsLayerDataSource other = (ImageWmsLayerDataSource) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getWidth(), other.getWidth()).
            append(getHeight(), other.getHeight()).
            append(getVersion(), other.getVersion()).
            append(getLayerNames(), other.getLayerNames()).
            append(getLayerStyles(), other.getLayerStyles()).
            isEquals();
    }

    public Boolean getRequestableByPath() {
        return requestableByPath;
    }

    public void setRequestableByPath( Boolean requestableByPath ) {
        this.requestableByPath = requestableByPath;
    }

    public String getCustomRequestPath() {
        return customRequestPath;
    }

    public void setCustomRequestPath( String customRequestPath ) {
        this.customRequestPath = customRequestPath;
    }
}
