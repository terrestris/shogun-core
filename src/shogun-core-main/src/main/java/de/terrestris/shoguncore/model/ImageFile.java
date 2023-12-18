package de.terrestris.shoguncore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Arrays;

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
    @SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
    public byte[] getThumbnail() {
        if (thumbnail == null) {
            return null;
        }
        return Arrays.copyOf(thumbnail, thumbnail.length);
    }

    /**
     * @param thumbnail the thumbnail to set
     */
    public void setThumbnail(byte[] thumbnail) {
        if (thumbnail == null) {
            this.thumbnail = null;
            return;
        }
        this.thumbnail = Arrays.copyOf(thumbnail, thumbnail.length);
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
        if (!(obj instanceof ImageFile)) {
            return false;
        }
        ImageFile other = (ImageFile) obj;

        return new EqualsBuilder().appendSuper(super.equals(other))
            .append(getWidth(), other.getWidth())
            .append(getHeight(), other.getHeight())
            .isEquals();
    }
}
