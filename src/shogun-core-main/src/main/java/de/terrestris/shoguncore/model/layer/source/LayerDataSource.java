package de.terrestris.shoguncore.model.layer.source;

import de.terrestris.shoguncore.model.PersistentObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * Base class for all layer datasources
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class LayerDataSource extends PersistentObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private String type;
    private String url;
    private String format;

    /**
     * default constructor
     */
    public LayerDataSource() {
        super();
    }

    /**
     * @param name
     * @param type
     * @param url
     */
    public LayerDataSource(String name, String type, String url, String format) {
        super();
        this.name = name;
        this.type = type;
        this.url = url;
        this.format = format;
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
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
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
        return new HashCodeBuilder(37, 13).
            appendSuper(super.hashCode()).
            append(getName()).
            append(getType()).
            append(getUrl()).
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
        if (!(obj instanceof LayerDataSource)) {
            return false;
        }
        LayerDataSource other = (LayerDataSource) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getName(), other.getName()).
            append(getType(), other.getType()).
            append(getUrl(), other.getUrl()).
            append(getFormat(), other.getFormat()).
            isEquals();
    }

}
