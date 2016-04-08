package de.terrestris.shogun2.model.security;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * @author Nils Bühner
 *
 */
@Entity
@Table
public class PermissionCollection extends PersistentObject {

	private static final long serialVersionUID = 1L;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<Permission> permissions = new HashSet<Permission>();

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public PermissionCollection() {
	}

	/**
	 *
	 * @param permissions
	 */
	public PermissionCollection(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the permissions
	 */
	public Set<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}
}
