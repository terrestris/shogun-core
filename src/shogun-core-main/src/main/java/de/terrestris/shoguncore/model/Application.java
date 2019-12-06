package de.terrestris.shoguncore.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.terrestris.shoguncore.converter.PluginIdResolver;
import de.terrestris.shoguncore.model.module.CompositeModule;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.joda.time.ReadableDateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class represents a (GIS-)application, which can be opened in a browser.
 * It mainly provides the initial configuration of the map.
 *
 * @author Nils Bühner
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Application extends PersistentObject {

    private static final long serialVersionUID = 1L;

    /**
     * The name of the application.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The description of the application
     */
    @Column
    private String description;

    /**
     * The language of the application.
     */
    @Column
    private Locale language;

    /**
     * Whether this application is open, meaning it is accessible to the general
     * non-authenticated public.
     */
    @Column
    private Boolean open = true;

    /**
     * Whether this application is currently accessible at all. Applications can
     * be disabled/enabled by setting this flag.
     */
    @Column
    private Boolean active = true;

    /**
     * The URL under which the application is accessible.
     */
    @Column
    private String url;

    /**
     *
     */
    @ManyToOne
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private CompositeModule viewport;

    /**
     * The plugins available in this application
     */
    @ManyToMany
    @JoinTable(
        joinColumns = {@JoinColumn(name = "APPLICATION_ID")},
        inverseJoinColumns = {@JoinColumn(name = "PLUGIN_ID")}
    )
    @OrderColumn(name = "IDX")
    // The List of layers will be serialized (JSON) as an array of ID values
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        resolver = PluginIdResolver.class
    )
    @JsonIdentityReference(alwaysAsId = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.SUBSELECT)
    private List<Plugin> plugins = new ArrayList<>();

    /**
     * Explicitly adding the default constructor as this is important, e.g. for
     * Hibernate: http://goo.gl/3Cr1pw
     */
    public Application() {
    }

    public Application(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Overwrite this getter to set the {@link JsonIgnore} value to false
     * for this subclass.
     */
    @Override
    @JsonIgnore(false)
    public ReadableDateTime getCreated() {
        return super.getCreated();
    }

    /**
     * Overwrite this getter to set the {@link JsonIgnore} value to false
     * for this subclass.
     */
    @Override
    @JsonIgnore(false)
    public ReadableDateTime getModified() {
        return super.getModified();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the viewport
     */
    public CompositeModule getViewport() {
        return viewport;
    }

    /**
     * @param viewport the viewport to set
     */
    public void setViewport(CompositeModule viewport) {
        this.viewport = viewport;
    }

    /**
     * @return the plugins
     */
    public List<Plugin> getPlugins() {
        return plugins;
    }

    /**
     * @param plugins the plugins to set
     */
    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    /**
     * @see java.lang.Object#hashCode()
     * <p>
     * According to http://stackoverflow.com/q/27581 it is recommended to
     * use only getter-methods when using ORM like Hibernate
     */
    @Override
    public int hashCode() {
        // two randomly chosen prime numbers
        return new HashCodeBuilder(29, 11).
            appendSuper(super.hashCode()).
            append(getName()).
            append(getDescription()).
            append(getLanguage()).
            append(getOpen()).
            append(getActive()).
            append(getUrl()).
            append(getViewport()).
            toHashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * <p>
     * According to http://stackoverflow.com/q/27581 it is recommended to
     * use only getter-methods when using ORM like Hibernate
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Application)) {
            return false;
        }
        Application other = (Application) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getName(), other.getName()).
            append(getDescription(), other.getDescription()).
            append(getLanguage(), other.getLanguage()).
            append(getOpen(), other.getOpen()).
            append(getActive(), other.getActive()).
            append(getUrl(), other.getUrl()).
            append(getViewport(), other.getViewport()).
            isEquals();
    }

}
