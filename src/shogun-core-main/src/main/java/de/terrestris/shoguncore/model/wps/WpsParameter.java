package de.terrestris.shoguncore.model.wps;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.terrestris.shoguncore.model.PersistentObject;

/**
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "classType")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class WpsParameter extends PersistentObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private final String classType;

    /**
     *
     */
    private String valueType;

    /**
     *
     */
    private String displayName;

    /**
     * A set of formats this {@link WpsParameter} supports, e.g. 'text/xml;
     * subtype=gml/3.1.1' or 'application/wkt' or 'xs:double'
     */
    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "WPSPARAM_ID"))
    @Column(name = "FORMAT_NAME")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private Set<String> supportedFormats = new HashSet<String>();

    /**
     * A set of geometry types this {@link WpsParameter} supports, e.g. 'geom',
     * 'point', 'line', 'poly', 'multipoint', 'multiline', 'multipoly'
     */
    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "WPSPARAM_ID"))
    @Column(name = "GEOMTYPE_NAME")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private Set<String> supportedGeometryTypes = new HashSet<String>();

    /**
     * Constructor
     */
    public WpsParameter() {
        this.classType = getClass().getName();
    }


    /**
     * @return the classType
     */
    public String getClassType() {
        return classType;
    }

    /**
     * @return the valueType
     */
    public String getValueType() {
        return valueType;
    }


    /**
     * @param valueType the valueType to set
     */
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }


    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    /**
     * @return the supportedFormats
     */
    public Set<String> getSupportedFormats() {
        return supportedFormats;
    }


    /**
     * @param supportedFormats the supportedFormats to set
     */
    public void setSupportedFormats(Set<String> supportedFormats) {
        this.supportedFormats = supportedFormats;
    }


    /**
     * @return the supportedGeometryTypes
     */
    public Set<String> getSupportedGeometryTypes() {
        return supportedGeometryTypes;
    }


    /**
     * @param supportedGeometryTypes the supportedGeometryTypes to set
     */
    public void setSupportedGeometryTypes(Set<String> supportedGeometryTypes) {
        this.supportedGeometryTypes = supportedGeometryTypes;
    }


    /**
     * @see java.lang.Object#hashCode()
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals-and-hashcode-in-java
     * it is recommended only to use getter-methods when using ORM like Hibernate
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 47) // two randomly chosen prime numbers
            .appendSuper(super.hashCode())
            .append(getValueType())
            .append(getDisplayName())
            .toHashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals-and-hashcode-in-java
     * it is recommended only to use getter-methods when using ORM like Hibernate
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WpsParameter))
            return false;
        WpsParameter other = (WpsParameter) obj;

        return new EqualsBuilder()
            .appendSuper(super.equals(other))
            .append(getValueType(), other.getValueType())
            .append(getDisplayName(), other.getDisplayName())
            .isEquals();
    }

    /**
     *
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .appendSuper(super.toString())
            .append("valueType", valueType)
            .append("displayName", displayName)
            .toString();
    }
}
