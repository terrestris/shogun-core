package de.terrestris.shoguncore.model.layer.appearance;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.terrestris.shoguncore.converter.PropertyValueConverter;
import de.terrestris.shoguncore.model.PersistentObject;
import de.terrestris.shoguncore.model.layer.Layer;

/**
 * This class holds the appearance properties of a layer {@link Layer} Object
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 */
@Entity
@Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LayerAppearance extends PersistentObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private String attribution;

    /**
     *
     */
    private Boolean hoverable;

    /**
     *
     */
    private String hoverTemplate;

    /**
     *
     */
    @ElementCollection
    @MapKeyColumn(name = "PROPERTY")
    @Column(name = "VALUE")
    @CollectionTable(joinColumns = @JoinColumn(name = "APPEARANCE_ID"))
    @Convert(converter = PropertyValueConverter.class, attributeName = "value")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private Map<String, Object> properties = new HashMap<>();

    /**
     *
     */
    private String name;

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
    private Double opacity;

    /**
     *
     */
    private Boolean visible;

    /**
     *
     */
    public LayerAppearance() {
        super();
    }

    /**
     * @param attribution
     * @param name
     * @param maxResolution
     * @param minResolution
     * @param opacity
     * @param visible
     */
    public LayerAppearance(String attribution, String name, Double maxResolution,
                           Double minResolution, Double opacity, Boolean visible) {
        super();
        this.attribution = attribution;
        this.name = name;
        this.maxResolution = maxResolution;
        this.minResolution = minResolution;
        this.opacity = opacity;
        this.visible = visible;
    }

    /**
     * @return the attribution
     */
    public String getAttribution() {
        return attribution;
    }

    /**
     * @param attribution the attribution to set
     */
    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    /**
     * @return the hoverable
     */
    public Boolean getHoverable() {
        return hoverable;
    }

    /**
     * @param hoverable the hoverable to set
     */
    public void setHoverable(Boolean hoverable) {
        this.hoverable = hoverable;
    }

    /**
     * @return the hoverTemplate
     */
    public String getHoverTemplate() {
        return hoverTemplate;
    }

    /**
     * @param hoverTemplate the hoverTemplate to set
     */
    public void setHoverTemplate(String hoverTemplate) {
        this.hoverTemplate = hoverTemplate;
    }

    /**
     * @return the properties
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
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
     * @return the opacity
     */
    public Double getOpacity() {
        return opacity;
    }

    /**
     * @param opacity the opacity to set
     */
    public void setOpacity(Double opacity) {
        this.opacity = opacity;
    }

    /**
     * @return the visible
     */
    public Boolean getVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
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
        return new HashCodeBuilder(31, 13).
            appendSuper(super.hashCode()).
            append(getAttribution()).
            append(getName()).
            append(getMaxResolution()).
            append(getMinResolution()).
            append(getOpacity()).
            append(getVisible()).
            append(getHoverable()).
            append(getHoverTemplate()).
            append(getProperties()).
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
        if (!(obj instanceof LayerAppearance))
            return false;
        LayerAppearance other = (LayerAppearance) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getAttribution(), other.getAttribution()).
            append(getName(), other.getName()).
            append(getMaxResolution(), other.getMaxResolution()).
            append(getMinResolution(), other.getMinResolution()).
            append(getOpacity(), other.getOpacity()).
            append(getVisible(), other.getVisible()).
            append(getHoverable(), other.getHoverable()).
            append(getHoverTemplate(), other.getHoverTemplate()).
            append(getProperties(), other.getProperties()).
            isEquals();
    }

}
