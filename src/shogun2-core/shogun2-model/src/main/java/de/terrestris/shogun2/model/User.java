package de.terrestris.shogun2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import ch.rasc.extclassgenerator.Model;

/**
 * @author Nils Bühner
 *
 */
@Entity
@Table
@Model(value = "shogun2.model.User",
	readMethod = "userService.findWithSortingAndPagingExtDirect",
	createMethod = "userService.saveOrUpdateCollection",
	updateMethod = "userService.saveOrUpdateCollection",
	destroyMethod = "userService.deleteCollection")
public class User extends Person {

	private static final long serialVersionUID = 1L;

	@Column(unique = true)
	private String accountName;

	@Column
	private String password;

	@Column
	private boolean active;

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
		return new HashCodeBuilder(23, 13).appendSuper(super.hashCode())
				.append(getAccountName()).append(getPassword())
				.append(isActive()).toHashCode();
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
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;

		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(getAccountName(), other.getAccountName())
				.append(getPassword(), other.getPassword())
				.append(isActive(), other.isActive()).isEquals();
	}

	/**
	 *
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.appendSuper(super.toString())
				.append("accountName", getAccountName())
				.append("password", getPassword())
				.append("active", isActive())
				.toString();
	}
}
