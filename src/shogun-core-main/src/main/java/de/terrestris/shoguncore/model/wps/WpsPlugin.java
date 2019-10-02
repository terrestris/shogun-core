package de.terrestris.shoguncore.model.wps;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.terrestris.shoguncore.converter.WpsProcessExecuteIdResolver;
import de.terrestris.shoguncore.model.Plugin;
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
public class WpsPlugin extends Plugin {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ManyToOne
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        resolver = WpsProcessExecuteIdResolver.class
    )
    @JsonIdentityReference(alwaysAsId = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private WpsProcessExecute process;

    /**
     * Constructor
     */
    public WpsPlugin() {
    }

    /**
     * @return the process
     */
    public WpsProcessExecute getProcess() {
        return process;
    }


    /**
     * @param process the process to set
     */
    public void setProcess(WpsProcessExecute process) {
        this.process = process;
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
        return new HashCodeBuilder(19, 59) // two randomly chosen prime numbers
            .appendSuper(super.hashCode())
            .append(getProcess())
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
        if (!(obj instanceof WpsPlugin)) {
            return false;
        }
        WpsPlugin other = (WpsPlugin) obj;

        return new EqualsBuilder()
            .appendSuper(super.equals(other))
            .append(getProcess(), other.getProcess())
            .isEquals();
    }

    /**
     *
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .appendSuper(super.toString())
            .append("process", process)
            .toString();
    }
}
