/**
 *
 */
package de.terrestris.shoguncore.model.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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
 * A search module working with the OSM Nominatim Service.
 *
 * @author Kai Volland
 */
@Entity
@Table
@Cacheable
public class NominatimSearch extends Module {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * The response format.
     */
    @Enumerated(EnumType.STRING)
    private NominatimFormatType format;
    /**
     * Limits the response.
     */
    private Integer resultLimit;
    /**
     * A list of EPSG-Codes the should be available in the module.
     */
    @ElementCollection
    @CollectionTable(joinColumns = @JoinColumn(name = "NOMINATIM_ID"))
    @Column(name = "VIEWBOXINTEGER")
    @OrderColumn(name = "IDX")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private List<Integer> viewboxlbrt = new ArrayList<Integer>();
    /**
     * Characters needed to send a request.
     */
    private Integer minSearchTextChars;
    /**
     * The delay between hitting a key and sending the request in ms.
     */
    private Integer typeDelay;
    /**
     * The template of the grouping Header.
     * See: http://docs.sencha.com/extjs/6.0/6.0.0-classic/#!/api/Ext.grid.feature.Grouping-cfg-groupHeaderTpl
     */
    private String groupHeaderTpl;

    /**
     * Explicitly adding the default constructor as this is important, e.g. for
     * Hibernate: http://goo.gl/3Cr1pw
     */
    public NominatimSearch() {
    }

    /**
     * @return the format
     */
    public NominatimFormatType getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(NominatimFormatType format) {
        this.format = format;
    }

    /**
     * @return the resultLimit
     */
    public Integer getResultLimit() {
        return resultLimit;
    }

    /**
     * @param resultLimit the resultLimit to set
     */
    public void setResultLimit(Integer resultLimit) {
        this.resultLimit = resultLimit;
    }

    /**
     * @return the viewboxlbrt
     */
    public List<Integer> getViewboxlbrt() {
        return viewboxlbrt;
    }

    /**
     * @param viewboxlbrt the viewboxlbrt to set
     */
    public void setViewboxlbrt(List<Integer> viewboxlbrt) {
        this.viewboxlbrt = viewboxlbrt;
    }

    /**
     * @return the minSearchTextChars
     */
    public Integer getMinSearchTextChars() {
        return minSearchTextChars;
    }

    /**
     * @param minSearchTextChars the minSearchTextChars to set
     */
    public void setMinSearchTextChars(Integer minSearchTextChars) {
        this.minSearchTextChars = minSearchTextChars;
    }

    /**
     * @return the typeDelay
     */
    public Integer getTypeDelay() {
        return typeDelay;
    }

    /**
     * @param typeDelay the typeDelay to set
     */
    public void setTypeDelay(Integer typeDelay) {
        this.typeDelay = typeDelay;
    }

    /**
     * @return the groupHeaderTpl
     */
    public String getGroupHeaderTpl() {
        return groupHeaderTpl;
    }

    /**
     * @param groupHeaderTpl the groupHeaderTpl to set
     */
    public void setGroupHeaderTpl(String groupHeaderTpl) {
        this.groupHeaderTpl = groupHeaderTpl;
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
        return new HashCodeBuilder(29, 3).
            appendSuper(super.hashCode()).
            append(getFormat()).
            append(getResultLimit()).
            append(getViewboxlbrt()).
            append(getMinSearchTextChars()).
            append(getTypeDelay()).
            append(getGroupHeaderTpl()).
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
        if (!(obj instanceof NominatimSearch)) {
            return false;
        }
        NominatimSearch other = (NominatimSearch) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getFormat(), other.getFormat()).
            append(getResultLimit(), other.getResultLimit()).
            append(getViewboxlbrt(), other.getViewboxlbrt()).
            append(getMinSearchTextChars(), other.getMinSearchTextChars()).
            append(getTypeDelay(), other.getTypeDelay()).
            append(getGroupHeaderTpl(), other.getGroupHeaderTpl()).
            isEquals();
    }

    /**
     * A enum type for the allowed response format.
     */
    public enum NominatimFormatType {
        HTML("html"),
        XML("xml"),
        JSON("json"),
        JSONV2("jsonv2");

        private final String value;

        /**
         * Enum constructor
         *
         * @param value
         */
        NominatimFormatType(String value) {
            this.value = value;
        }

        /**
         * Static method to get an enum based on a string value.
         * This method is annotated with {@link JsonCreator},
         * which allows the client to send case insensitive string
         * values (like "jSon"), which will be converted to the
         * correct enum value.
         *
         * @param inputValue
         * @return
         */
        @JsonCreator
        public static NominatimFormatType fromString(String inputValue) {
            if (inputValue != null) {
                for (NominatimFormatType type : NominatimFormatType.values()) {
                    if (inputValue.equalsIgnoreCase(type.value)) {
                        return type;
                    }
                }
            }
            return null;
        }

        /**
         * This method is annotated with {@link JsonValue},
         * so that jackson will serialize the enum value to
         * the (lowercase) {@link #value}.
         */
        @Override
        @JsonValue
        public String toString() {
            return value;
        }
    }

}
