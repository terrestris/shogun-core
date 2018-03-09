package de.terrestris.shogun2.model.token;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import de.terrestris.shogun2.model.User;

/**
 * A {@link Token} instance that has a one-to-one relation to a {@link User}
 * that wants to register.
 *
 * @author Daniel Koch
 * @author Nils Bühner
 */
@Entity
@Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RegistrationToken extends UserToken {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public RegistrationToken() {
    }

    /**
     * Constructor that uses the default expiration time.
     */
    public RegistrationToken(User user) {
        super(user);
    }

    /**
     * Constructor that uses the passed values
     */
    public RegistrationToken(User user, int expirationInMinutes) {
        super(user, expirationInMinutes);
    }

}
