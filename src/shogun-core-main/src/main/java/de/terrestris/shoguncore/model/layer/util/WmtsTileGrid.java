package de.terrestris.shoguncore.model.layer.util;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Class representing a WMTS tile grid
 *
 * @author Andre Henn
 */
@Entity
@Table
public class WmtsTileGrid extends TileGrid {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // String list holding the matrix IDs.
    @ElementCollection(targetClass = String.class)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<String> matrixIds;

    /**
     * @return the matrix IDs.
     */
    public List<String> getMatrixIds() {
        return matrixIds;
    }

    /**
     * Setter for matrix IDs
     *
     * @param matrixIds The list of matrix IDs to set
     */
    public void setMatrixIds(List<String> matrixIds) {
        this.matrixIds = matrixIds;
    }

    @Override
    public int hashCode() {
        // two randomly chosen prime numbers
        return new HashCodeBuilder(89, 29).
            appendSuper(super.hashCode()).
            append(getMatrixIds()).
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
        if (!(obj instanceof WmtsTileGrid)) {
            return false;
        }
        WmtsTileGrid other = (WmtsTileGrid) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getMatrixIds(), other.getMatrixIds()).
            isEquals();
    }

}
