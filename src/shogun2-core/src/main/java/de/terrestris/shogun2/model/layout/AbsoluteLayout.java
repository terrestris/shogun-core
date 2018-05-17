/**
 *
 */
package de.terrestris.shogun2.model.layout;

import java.awt.Point;
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

import de.terrestris.shogun2.model.module.CompositeModule;

/**
 * This class is the representation of an absolute layout, where components are
 * anchored in absolute positions, which are stored in the {@link #coords}
 * property.
 * <p>
 * The order of the {@link #coords} should match the order of the corresponding
 * {@link CompositeModule#getSubModules()}.
 *
 * @author Nils Bühner
 */
@Table
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AbsoluteLayout extends Layout {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Explicitly adding the default constructor as this is important, e.g. for
     * Hibernate: http://goo.gl/3Cr1pw
     */
    public AbsoluteLayout() {
        this.setType("absolute");
    }

    /**
     *
     */
    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "LAYOUT_ID"))
    @Column(name = "COORD")
    @OrderColumn(name = "IDX")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private List<Point> coords = new ArrayList<Point>();

    /**
     * @return the coords
     */
    public List<Point> getCoords() {
        return coords;
    }

    /**
     * @param coords the coords to set
     */
    public void setCoords(List<Point> coords) {
        this.coords = coords;
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
        return new HashCodeBuilder(11, 13).
            appendSuper(super.hashCode()).
            append(getCoords()).
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
        if (!(obj instanceof AbsoluteLayout))
            return false;
        AbsoluteLayout other = (AbsoluteLayout) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getCoords(), other.getCoords()).
            isEquals();
    }

}
