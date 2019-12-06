/**
 *
 */
package de.terrestris.shoguncore.model.module;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The CoordinateTransform module allows the user to transform map coordinates into
 * different projections.
 *
 * @author Kai Volland
 */
@Entity
@Table
@Cacheable
public class CoordinateTransformation extends Module {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * A list of EPSG-Codes that should be available in the module.
     */
    @ElementCollection
    @CollectionTable(
        name = "COORDINATETRANSFORMATIONS_EPSG",
        joinColumns = @JoinColumn(name = "COORDTRANS_ID"))
    @Column(name = "EPSG")
    @OrderColumn(name = "IDX")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private List<String> epsgCodes = new ArrayList<String>();

    /**
     * Whether the form should be filled on instantiation or not.
     */
    private Boolean transformCenterOnRender;

    /**
     * Explicitly adding the default constructor as this is important, e.g. for
     * Hibernate: http://goo.gl/3Cr1pw
     */
    public CoordinateTransformation() {
    }

    /**
     * @return the epsgCodes
     */
    public List<String> getEpsgCodes() {
        return epsgCodes;
    }

    /**
     * @param epsgCodes the epsgCodes to set
     */
    public void setEpsgCodes(List<String> epsgCodes) {
        this.epsgCodes = epsgCodes;
    }

    /**
     * @return the transformCenterOnRender
     */
    public Boolean getTransformCenterOnRender() {
        return transformCenterOnRender;
    }

    /**
     * @param transformCenterOnRender the transformCenterOnRender to set
     */
    public void setTransformCenterOnRender(Boolean transformCenterOnRender) {
        this.transformCenterOnRender = transformCenterOnRender;
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
        return new HashCodeBuilder(19, 3).
            appendSuper(super.hashCode()).
            append(getEpsgCodes()).
            append(getTransformCenterOnRender()).
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
        if (!(obj instanceof CoordinateTransformation)) {
            return false;
        }
        CoordinateTransformation other = (CoordinateTransformation) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getEpsgCodes(), other.getEpsgCodes()).
            append(getTransformCenterOnRender(), other.getTransformCenterOnRender()).
            isEquals();
    }

}
