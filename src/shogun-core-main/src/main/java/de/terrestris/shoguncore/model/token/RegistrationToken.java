package de.terrestris.shoguncore.model.token;

import de.terrestris.shoguncore.model.User;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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
