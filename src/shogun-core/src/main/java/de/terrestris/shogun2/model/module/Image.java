/**
 *
 */
package de.terrestris.shogun2.model.module;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The Image Module is the Ext JS representation of an HTML img element.
 *
 * @author Kai Volland
 */
@Entity
@Table
@Cacheable
public class Image extends Module {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The path to the image;
     */
    private String src;

    /**
     * An optional link, if the image is clickable.
     */
    private String link;

    /**
     * The alternative Text for the Image.
     */
    private String altText;

    /**
     * Explicitly adding the default constructor as this is important, e.g. for
     * Hibernate: http://goo.gl/3Cr1pw
     */
    public Image() {
    }

    /**
     * @return the src
     */
    public String getSrc() {
        return src;
    }

    /**
     * @param src the src to set
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the altText
     */
    public String getAltText() {
        return altText;
    }

    /**
     * @param altText the altText to set
     */
    public void setAltText(String altText) {
        this.altText = altText;
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
        return new HashCodeBuilder(7, 3).
            appendSuper(super.hashCode()).
            append(getSrc()).
            append(getLink()).
            append(getAltText()).
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
        if (!(obj instanceof Image))
            return false;
        Image other = (Image) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getSrc(), other.getSrc()).
            append(getLink(), other.getLink()).
            append(getAltText(), other.getAltText()).
            isEquals();
    }

}
