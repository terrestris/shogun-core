/**
 *
 */
package de.terrestris.shoguncore.model.layout;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.terrestris.shoguncore.model.module.CompositeModule;

/**
 * This class is the representation of an border layout, where components are
 * anchored in (predefined) regions, which are stored in the {@link #regions}
 * property.
 * <p>
 * The order of the {@link #regions} should match the order of the corresponding
 * {@link CompositeModule#getSubModules()}.
 *
 * @author Nils Bühner
 */
@Table
@Entity
@Cacheable
public class BorderLayout extends Layout {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Explicitly adding the default constructor as this is important, e.g. for
     * Hibernate: http://goo.gl/3Cr1pw
     */
    public BorderLayout() {
        this.setType("border");
    }

    /**
     *
     */
    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "LAYOUT_ID"))
    @Column(name = "REGION")
    @OrderColumn(name = "IDX")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private List<String> regions = new ArrayList<String>();

    /**
     * @return the regions
     */
    public List<String> getRegions() {
        return regions;
    }

    /**
     * @param regions the regions to set
     */
    public void setRegions(List<String> regions) {
        this.regions = regions;
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
        return new HashCodeBuilder(53, 23).
            appendSuper(super.hashCode()).
            append(getRegions()).
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
        if (!(obj instanceof BorderLayout))
            return false;
        BorderLayout other = (BorderLayout) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getRegions(), other.getRegions()).
            isEquals();
    }

}
