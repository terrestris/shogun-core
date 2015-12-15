package de.terrestris.shogun2.model.token;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;

/**
 * Base class for all tokens.
 *
 * @author Daniel Koch
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Token extends PersistentObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The token validity period (in hours).
	 *
	 * The Default is to 24.
	 *
	 */
	private static final int expiration = 24;

	/**
	 * The token string itself.
	 */
	private String token;

	/**
	 * The expiration date of the token. Will be set
	 */
	@Column(updatable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private final ReadableDateTime expirationDate;

	/**
	 * The user who has requested the token. Hereby one user can have one
	 * token and one token can be used by one user (at the same time) only.
	 */
	@OneToOne
	@JoinColumn(name = "USER_ID")
	private User user;

	/**
	 * Constructor
	 */
	protected Token(int expiration) {
		// set the expiration date
		this.expirationDate = DateTime.now().plusHours(expiration);
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the expiration
	 */
	public static int getExpiration() {
		return expiration;
	}

	/**
	 * @return the expirationDate
	 */
	public ReadableDateTime getExpirationDate() {
		return expirationDate;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(17, 37).
				appendSuper(super.hashCode()).
				append(getToken()).
				append(getExpirationDate()).
				append(getUser()).
				toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
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
				append(getUser(), other.getUser()).
				isEquals();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
