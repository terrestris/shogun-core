/**
 *
 */
package de.terrestris.shoguncore.model.module;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A module which contains a formular to print the map with the mapfish print v3.
 *
 * @author Kai Volland
 */
@Entity
@Table
@Cacheable
public class Print extends Module {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The mapfish-print url.
     */
    private String url;

    /**
     * Explicitly adding the default constructor as this is important, e.g. for
     * Hibernate: http://goo.gl/3Cr1pw
     */
    public Print() {
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
     * @see java.lang.Object#hashCode()
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
     */
    public int hashCode() {
        // two randomly chosen prime numbers
        return new HashCodeBuilder(23, 3).
            appendSuper(super.hashCode()).
            append(getUrl()).
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
        if (!(obj instanceof Print))
            return false;
        Print other = (Print) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getUrl(), other.getUrl()).
            isEquals();
    }

}
