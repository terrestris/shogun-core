package de.terrestris.shogun2.model.token;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.terrestris.shogun2.model.User;

/**
 * A {@link UserToken} instance that has a one-to-one relation to a {@link User}
 * .
 *
 * @author Daniel Koch
 * @author Nils Bühner
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class UserToken extends Token {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The default expiration in minutes
     */
    private static final int DEFAULT_EXPIRATION_MINUTES = 60;

    /**
     * The user who has requested the token. Hereby one user can have one
     * token and one token can be used by one user (at the same time) only.
     */
    @OneToOne
    @JoinColumn(name = "USER_ID")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private final User user;

    /**
     * Default constructor
     */
    protected UserToken() {
        this(null);
    }

    /**
     * Constructor. Uses the {@link #DEFAULT_EXPIRATION_MINUTES} value.
     *
     * @param The user.
     */
    protected UserToken(User user) {
        this(user, DEFAULT_EXPIRATION_MINUTES);
    }

    /**
     * Constructor
     *
     * @param user                The user
     * @param expirationInMinutes The expiration period in minutes
     */
    protected UserToken(User user, int expirationInMinutes) {
        // call super constructor
        super(expirationInMinutes);

        // set the user
        this.user = user;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
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
        return new HashCodeBuilder(17, 37).
            appendSuper(super.hashCode()).
            append(getUser()).
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
        if (!(obj instanceof UserToken))
            return false;
        UserToken other = (UserToken) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getUser(), other.getUser()).
            isEquals();
    }

}
