package de.terrestris.shogun2.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.terrestris.shogun2.model.security.PermissionCollection;

/**
 * This class represents the abstract superclass for all entities that may be
 * secured in the sense of a restriction to certain users or groups.
 *
 * @author Nils Bühner
 *
 */
@MappedSuperclass
public abstract class SecuredPersistentObject extends PersistentObject {

	private static final long serialVersionUID = 1L;

	@OneToMany
	@JsonIgnore
	private Map<User, PermissionCollection> userPermissions = new HashMap<User, PermissionCollection>();

	@OneToMany
	@JsonIgnore
	private Map<UserGroup, PermissionCollection> groupPermissions = new HashMap<UserGroup, PermissionCollection>();

	/**
	 * @return the userPermissions
	 */
	public Map<User, PermissionCollection> getUserPermissions() {
		return userPermissions;
	}

	/**
	 * @param userPermissions the userPermissions to set
	 */
	public void setUserPermissions(Map<User, PermissionCollection> userPermissions) {
		this.userPermissions = userPermissions;
	}

	/**
	 * @return the groupPermissions
	 */
	public Map<UserGroup, PermissionCollection> getGroupPermissions() {
		return groupPermissions;
	}

	/**
	 * @param groupPermissions the groupPermissions to set
	 */
	public void setGroupPermissions(
			Map<UserGroup, PermissionCollection> groupPermissions) {
		this.groupPermissions = groupPermissions;
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
		// do not use the permission maps here to avoid performance problems
		// (as we are loading everything lazily). Subclasses should
		// overwrite/implement this method instead and use their basic
		// properties for calculation

		// two randomly chosen prime numbers
		return new HashCodeBuilder(13, 23).
				appendSuper(super.hashCode()).
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
		// do not use the permission maps here to avoid performance problems
		// (as we are loading everything lazily). Subclasses should
		// overwrite/implement this method instead and use their basic
		// properties for calculation

		if (!(obj instanceof SecuredPersistentObject))
			return false;
		SecuredPersistentObject other = (SecuredPersistentObject) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		// do not use the permission maps here to avoid performance problems
		// (as we are loading everything lazily). Subclasses should
		// overwrite/implement this method instead and use their basic
		// properties for calculation

		return new ToStringBuilder(this)
			.appendSuper(super.toString())
			.toString();
	}

}
