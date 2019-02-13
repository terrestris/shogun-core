package de.terrestris.shogun2.model.wps;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.terrestris.shogun2.model.Plugin;

/**
 *
 */
@Entity
@Table
@Cacheable
public class WpsPrimitive extends WpsParameter {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private Plugin inputPlugin;

    /**
     * Constructor
     */
    public WpsPrimitive() {
    }

    /**
     * @return the inputPlugin
     */
    public Plugin getInputPlugin() {
        return inputPlugin;
    }

    /**
     * @param inputPlugin the inputPlugin to set
     */
    public void setInputPlugin(Plugin inputPlugin) {
        this.inputPlugin = inputPlugin;
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
        return new HashCodeBuilder(13, 47) // two randomly chosen prime numbers
            .appendSuper(super.hashCode())
            .append(getInputPlugin())
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
        if (!(obj instanceof WpsPrimitive))
            return false;
        WpsPrimitive other = (WpsPrimitive) obj;

        return new EqualsBuilder()
            .appendSuper(super.equals(other))
            .append(getInputPlugin(), other.getInputPlugin())
            .isEquals();
    }

    /**
     *
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .appendSuper(super.toString())
            .append("inputPlugin", inputPlugin)
            .toString();
    }
}
