package de.terrestris.shogun2.model.token;

import javax.persistence.Entity;
import javax.persistence.Table;

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
public class PasswordResetToken extends UserToken {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public PasswordResetToken() {
	}

	/**
	 * Constructor that uses the default expiration time.
	 */
	public PasswordResetToken(User user) {
		super(user);
	}

	/**
	 * Constructor that uses the passed values
	 */
	public PasswordResetToken(User user, int expirationInMinutes) {
		super(user, expirationInMinutes);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
