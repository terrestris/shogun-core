package de.terrestris.shoguncore.model.wps;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table
@Cacheable
public class WpsReference extends WpsParameter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private String url;

    /**
     *
     */
    private String mimeType;

    /**
     *
     */
    private String method;

    /**
     *
     */
    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private WpsParameter body;

    /**
     * Constructor
     */
    public WpsReference() {
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
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }


    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }


    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }


    /**
     * @return the body
     */
    public WpsParameter getBody() {
        return body;
    }


    /**
     * @param body the body to set
     */
    public void setBody(WpsParameter body) {
        this.body = body;
    }


    /**
     * @see java.lang.Object#hashCode()
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals-and-hashcode-in-java
     * it is recommended only to use getter-methods when using ORM like Hibernate
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 47) // two randomly chosen prime numbers
            .appendSuper(super.hashCode())
            .append(getUrl())
            .append(getMimeType())
            .append(getMethod())
            .toHashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals-and-hashcode-in-java
     * it is recommended only to use getter-methods when using ORM like Hibernate
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WpsReference)) {
            return false;
        }
        WpsReference other = (WpsReference) obj;

        return new EqualsBuilder()
            .appendSuper(super.equals(other))
            .append(getUrl(), other.getUrl())
            .append(getMimeType(), other.getMimeType())
            .append(getMethod(), other.getMethod())
            .isEquals();
    }

    /**
     *
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .appendSuper(super.toString())
            .append("url", url)
            .append("mimeType", mimeType)
            .append("method", method)
            .toString();
    }
}
