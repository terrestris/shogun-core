/**
 *
 */
package de.terrestris.shogun2.model.layout;

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
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.module.CompositeModule;
import de.terrestris.shogun2.model.module.Module;

/**
 * This class represents the layout of a {@link CompositeModule} in a GUI.
 * It provides {@link #propertyHints}, which are (names) of <b>recommended</b>
 * properties for the children of the corresponding {@link CompositeModule} and
 * {@link #propertyMusts}, which are (names) of <b>required</b> properties for
 * the children of the {@link CompositeModule}. The values of such properties
 * should be stored in the child {@link Module}s property map.
 * ({@link Module#setProperties(java.util.Map)})
 *
 * @author Nils Bühner
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Layout extends PersistentObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The type of the layout, e.g. "border", "absolute", "hbox" or "vbox".
     */
    private String type;

    /**
     * A set of property names that are <b>recommended</b> for the use in the
     * related child modules. {@link CompositeModule#getSubModules()}.
     */
    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "LAYOUT_ID"))
    @Column(name = "PROPERTYNAME")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private Set<String> propertyHints = new HashSet<String>();

    /**
     * A set of property names that are <b>required</b> for the use in the
     * related child modules. {@link CompositeModule#getSubModules()}.
     */
    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "LAYOUT_ID"))
    @Column(name = "PROPERTYNAME")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private Set<String> propertyMusts = new HashSet<String>();

    /**
     * Explicitly adding the default constructor as this is important, e.g. for
     * Hibernate: http://goo.gl/3Cr1pw
     */
    public Layout() {
    }

    /**
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the propertyHints
     */
    public Set<String> getPropertyHints() {
        return propertyHints;
    }

    /**
     * @param propertyHints the propertyHints to set
     */
    public void setPropertyHints(Set<String> propertyHints) {
        this.propertyHints = propertyHints;
    }

    /**
     * @return the propertyMusts
     */
    public Set<String> getPropertyMusts() {
        return propertyMusts;
    }

    /**
     * @param propertyMusts the propertyMusts to set
     */
    public void setPropertyMusts(Set<String> propertyMusts) {
        this.propertyMusts = propertyMusts;
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
        return new HashCodeBuilder(13, 7).
            appendSuper(super.hashCode()).
            append(getType()).
            append(getPropertyHints()).
            append(getPropertyMusts()).
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
        if (!(obj instanceof Layout))
            return false;
        Layout other = (Layout) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getType(), other.getType()).
            append(getPropertyHints(), other.getPropertyHints()).
            append(getPropertyMusts(), other.getPropertyMusts()).
            isEquals();
    }

}
