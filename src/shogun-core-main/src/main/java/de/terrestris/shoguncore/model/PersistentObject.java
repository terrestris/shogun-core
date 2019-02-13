/**
 * Inspired by
 * http://ocpsoft.org/java/hibernate-use-a-base-class-to-map-common-fields/
 */
package de.terrestris.shoguncore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.terrestris.shoguncore.model.security.PermissionCollection;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the abstract superclass for all entities that are
 * persisted in the database.
 * <p>
 * Subclasses of this class can further be inherited and there should be no
 * problems with hibernate mappings/database interactions.
 *
 * @author Nils Bühner
 */
@MappedSuperclass
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class PersistentObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    // The column annotation is used by hibernate for the column creation, e.g.
    // to build constraints like nullable
    @Column(updatable = false, nullable = false)
    private final Integer id = null;

    /**
     * The getter of this property {@link #getCreated()} is annotated with
     * {@link JsonIgnore}. This way, the annotation can be overwritten in
     * subclasses.
     */
    @Column(updatable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private final ReadableDateTime created;

    /**
     * The getter of this property {@link #getModified()} is annotated with
     * {@link JsonIgnore}. This way, the annotation can be overwritten in
     * subclasses.
     */
    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private ReadableDateTime modified;

    /**
     *
     */
    @ManyToMany
    @JoinTable(
        name = "USERPERMISSIONS",
        joinColumns = @JoinColumn(name = "ENTITY_ID"))
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private Map<User, PermissionCollection> userPermissions = new HashMap<User, PermissionCollection>();

    /**
     *
     */
    @ManyToMany
    @JoinTable(
        name = "GROUPPERMISSIONS",
        joinColumns = @JoinColumn(name = "ENTITY_ID"))
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private Map<UserGroup, PermissionCollection> groupPermissions = new HashMap<UserGroup, PermissionCollection>();

    /**
     * Constructor
     */
    protected PersistentObject() {
        this.created = DateTime.now();
        this.modified = DateTime.now();
    }

    /**
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     * Ignore the {@link #created} property when de-/serializing.
     * This can be overwritten in subclasses.
     *
     * @return The date of the creation of the entity.
     */
    @JsonIgnore
    public ReadableDateTime getCreated() {
        return created;
    }

    /**
     * Ignore the {@link #modified} property when de-/serializing.
     * This can be overwritten in subclasses.
     *
     * @return The date of the last modification of the entity.
     */
    @JsonIgnore
    public ReadableDateTime getModified() {
        return modified;
    }

    /**
     * @param modified
     */
    public void setModified(ReadableDateTime modified) {
        this.modified = modified;
    }

    /**
     * @return the userPermissions
     */
    public Map<User, PermissionCollection> getUserPermissions() {
        return userPermissions;
    }

    /**
     * @param userPermissions the userPermissions to set
     */
    public void setUserPermissions(Map<User, PermissionCollection> userPermissions) {
        this.userPermissions = userPermissions;
    }

    /**
     * @return the groupPermissions
     */
    public Map<UserGroup, PermissionCollection> getGroupPermissions() {
        return groupPermissions;
    }

    /**
     * @param groupPermissions the groupPermissions to set
     */
    public void setGroupPermissions(
        Map<UserGroup, PermissionCollection> groupPermissions) {
        this.groupPermissions = groupPermissions;
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
        return new HashCodeBuilder(17, 43).
            append(getClass()).
            append(getCreated()).
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
        if (!(obj instanceof PersistentObject))
            return false;
        PersistentObject other = (PersistentObject) obj;

        return new EqualsBuilder().
            append(getClass(), other.getClass()).
            append(getCreated(), other.getCreated()).
            isEquals();
    }

    /**
     *
     */
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", getId())
            .append("created", getCreated())
            .append("modified", getModified())
            .toString();
    }

}
