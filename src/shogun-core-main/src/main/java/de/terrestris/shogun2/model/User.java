package de.terrestris.shogun2.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.terrestris.shogun2.converter.UserGroupIdResolver;

/**
 * @author Nils Bühner
 */
@Entity
@Table
@Cacheable
public class User extends Person {

    private static final long serialVersionUID = 1L;

    @Column(unique = true)
    private String accountName;

    @Column
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    @Column
    private boolean active;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(
        joinColumns = {@JoinColumn(name = "USER_ID")},
        inverseJoinColumns = {@JoinColumn(name = "ROLE_ID")}
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Role> roles = new HashSet<Role>();

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @JoinTable(
        name = "user_usergroup",
        joinColumns = {@JoinColumn(name = "USER_ID")},
        inverseJoinColumns = {@JoinColumn(name = "USERGROUP_ID")}
    )
    @OrderColumn(name = "IDX")
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        resolver = UserGroupIdResolver.class
    )
    @JsonIdentityReference(alwaysAsId = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserGroup> userGroups = new HashSet<UserGroup>();

    /**
     * Default constructor
     */
    public User() {
    }

    public User(String firstName, String lastName, String accountName) {
        super(firstName, lastName);
        this.accountName = accountName;
    }

    public User(String firstName, String lastName, String accountName,
                String password) {
        super(firstName, lastName);
        this.accountName = accountName;
        this.password = password;
    }

    public User(String firstName, String lastName, String accountName,
                String password, boolean active) {
        super(firstName, lastName);
        this.accountName = accountName;
        this.password = password;
        this.active = active;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the roles
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * @return the userGroups
     */
    public Set<UserGroup> getUserGroups() {
        return userGroups;
    }

    /**
     * @param userGroups the userGroups to set
     */
    public void setUserGroups(Set<UserGroup> userGroups) {
        this.userGroups = userGroups;
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
        return new HashCodeBuilder(23, 13).
            appendSuper(super.hashCode()).
            append(getAccountName()).
            append(getPassword()).
            append(isActive()).
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
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User))
            return false;
        User other = (User) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getAccountName(), other.getAccountName()).
            append(getPassword(), other.getPassword()).
            append(isActive(), other.isActive()).
            isEquals();
    }

    /**
     *
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("accountName", getAccountName())
            .append("isActive", isActive())
            .toString();
    }
}
