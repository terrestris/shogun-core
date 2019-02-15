package de.terrestris.shoguncore.model.layer.source;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Data source of <a href= "http://openlayers.org/en/master/apidoc/ol.layer.Vector.html">OpenLayers 3 vector layer</a>
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 */
@Entity
@Table
@Cacheable
public class VectorLayerDataSource extends LayerDataSource {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String format;

    /**
     *
     */
    public VectorLayerDataSource() {
        super();
    }

    /**
     * @param name
     * @param type
     * @param url
     * @param format
     */
    public VectorLayerDataSource(String name, String type, String url, String format) {
        super(name, type, url);
        this.setFormat(format);
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
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
        return new HashCodeBuilder(53, 13).
            appendSuper(super.hashCode()).
            append(getFormat()).
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
        if (!(obj instanceof VectorLayerDataSource))
            return false;
        VectorLayerDataSource other = (VectorLayerDataSource) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getFormat(), other.getFormat()).
            isEquals();
    }

}
