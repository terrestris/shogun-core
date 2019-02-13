/**
 *
 */
package de.terrestris.shogun2.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class represents an Image which is stored as a bytearray in the database
 *
 * @author Johannes Weskamm
 * @author Daniel Koch
 */
@Entity
@Table
@Cacheable
public class ImageFile extends File {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @JsonIgnore
    @Column(length = Integer.MAX_VALUE)
    private byte[] thumbnail;

    /**
     *
     */
    private Integer width;

    /**
     *
     */
    private Integer height;

    /**
     * @return the thumbnail
     */
    public byte[] getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail the thumbnail to set
     */
    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(Integer height) {
        this.height = height;
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
        return new HashCodeBuilder(499, 269).appendSuper(super.hashCode())
            .append(getWidth())
            .append(getHeight())
            .toHashCode();
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
        if (!(obj instanceof ImageFile))
            return false;
        ImageFile other = (ImageFile) obj;

        return new EqualsBuilder().appendSuper(super.equals(other))
            .append(getWidth(), other.getWidth())
            .append(getHeight(), other.getHeight())
            .isEquals();
    }
}
