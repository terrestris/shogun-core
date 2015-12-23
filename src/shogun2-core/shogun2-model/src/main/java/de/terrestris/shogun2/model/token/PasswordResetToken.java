package de.terrestris.shogun2.model.token;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.User;

/**
 *
 * A {@link Token} instance that has a one-to-one relation to a {@link User}
 * that wants to reset the password.
 *
 * @author Daniel Koch
 * @author Nils Bühner
 *
 */
@Entity
@Table
public class PasswordResetToken extends Token {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The default expiration in hours
	 */
	private static final int DEFAULT_EXPIRATION_HOURS = 48;

	/**
	 * The user who has requested the token. Hereby one user can have one
	 * token and one token can be used by one user (at the same time) only.
	 */
	@OneToOne
	@JoinColumn(name = "USER_ID")
	private final User user;

	/**
	 * Constructor. Uses the {@link #DEFAULT_EXPIRATION_HOURS} value.
	 *
	 * @param The user that wants to reset the password.
	 */
	public PasswordResetToken(User user) {
		this(user, DEFAULT_EXPIRATION_HOURS);
	}

	/**
	 * Constructor
	 *
	 * @param user The user that wants to reset the password.
	 * @param expirationInHours The expiration period in hours
	 */
	public PasswordResetToken(User user, int expirationInHours) {
		// call super constructor
		super(expirationInHours);

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
		if (!(obj instanceof PasswordResetToken))
			return false;
		PasswordResetToken other = (PasswordResetToken) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
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
