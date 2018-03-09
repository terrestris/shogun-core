package de.terrestris.shogun2.model.layer;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.layer.appearance.LayerAppearance;
import de.terrestris.shogun2.model.layer.source.LayerDataSource;

/**
 * Representation of a layer which consists a corresponding data source
 * and an appearance
 *
 * @author Kai Volland
 * @author Nils Bühner
 * @author terrestris GmbH & Co. KG
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Layer extends PersistentObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String description;

    /**
     *
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private LayerDataSource source;

    /**
     *
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private LayerAppearance appearance;

    /**
     *
     */
    public Layer() {
        super();
    }

    /**
     * @param name Layer name
     */
    public Layer(String name) {
        super();
        this.name = name;
    }

    /**
     * @param name       Layer name
     * @param source
     * @param appearance
     */
    public Layer(String name, LayerDataSource source, LayerAppearance appearance) {
        super();
        this.name = name;
        this.source = source;
        this.appearance = appearance;
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the source
     */
    public LayerDataSource getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(LayerDataSource source) {
        this.source = source;
    }

    /**
     * @return the appearance
     */
    public LayerAppearance getAppearance() {
        return appearance;
    }

    /**
     * @param appearance the appearance to set
     */
    public void setAppearance(LayerAppearance appearance) {
        this.appearance = appearance;
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
        return new HashCodeBuilder(29, 13).
            appendSuper(super.hashCode()).
            append(getName()).
            append(getDescription()).
            append(getSource()).
            append(getAppearance()).
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
        if (!(obj instanceof Layer))
            return false;
        Layer other = (Layer) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getName(), other.getName()).
            append(getDescription(), other.getDescription()).
            append(getSource(), other.getSource()).
            append(getAppearance(), other.getAppearance()).
            isEquals();
    }

}
