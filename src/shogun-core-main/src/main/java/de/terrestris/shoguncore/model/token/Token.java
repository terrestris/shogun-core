package de.terrestris.shoguncore.model.token;

import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

import de.terrestris.shoguncore.model.PersistentObject;

/**
 * Abstract base class for all tokens. A UUID token will be generated on
 * instantiation. An expiration period has to be given in constructor.
 *
 * @author Daniel Koch
 * @author Nils Bühner
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class Token extends PersistentObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The (unique) token string itself.
     */
    @Column(unique = true, updatable = false)
    private final String token;

    /**
     * The expiration date of the token. Will be set
     */
    @Column(updatable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private final ReadableDateTime expirationDate;

    /**
     * Constructor
     *
     * @param expirationInMinutes The expiration period in minutes
     */
    protected Token(int expirationInMinutes) {
        // call super constructor to assure that created/modified is set
        super();

        // create token
        this.token = UUID.randomUUID().toString();

        // set the expiration date
        this.expirationDate = ((DateTime) getCreated()).plusMinutes(expirationInMinutes);
    }

    /**
     * Helper method that returns true, if the token is expired in the given
     * number of minutes (starting from the current point of time).
     *
     * @param minutes
     * @return Whether or not the token expires within the given number of
     * minutes.
     */
    public boolean expiresWithin(int minutes) {

        DateTime dateToCheck = DateTime.now().plusMinutes(minutes);
        boolean isExpired = dateToCheck.isAfter(this.expirationDate);

        return isExpired;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the expirationDate
     */
    public ReadableDateTime getExpirationDate() {
        return expirationDate;
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
        return new HashCodeBuilder(17, 29).
            appendSuper(super.hashCode()).
            append(getToken()).
            append(getExpirationDate()).
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
        if (!(obj instanceof Token))
            return false;
        Token other = (Token) obj;

        return new EqualsBuilder().
            appendSuper(super.equals(other)).
            append(getToken(), other.getToken()).
            append(getExpirationDate(), other.getExpirationDate()).
            isEquals();
    }

}
