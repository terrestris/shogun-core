package de.terrestris.shoguncore.model.security;

import de.terrestris.shoguncore.model.PersistentObject;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nils Bühner
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PermissionCollection extends PersistentObject {

    private static final long serialVersionUID = 1L;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private Set<Permission> permissions = new HashSet<Permission>();

    /**
     * Explicitly adding the default constructor as this is important, e.g. for
     * Hibernate: http://goo.gl/3Cr1pw
     */
    public PermissionCollection() {
    }

    /**
     * @param permissions
     */
    public PermissionCollection(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * @return the permissions
     */
    public Set<Permission> getPermissions() {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
