package de.terrestris.shogun2.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

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
//	@CollectionTable(name = "ENTITY_USERPERMISSIONS", joinColumns = @JoinColumn(name = "ENTITY_ID"))
//	@JoinTable(joinColumns = @JoinColumn(name = "ENTITY_ID"))
	@Cascade(CascadeType.ALL)
	@JsonIgnore
//	@MapKeyJoinColumn(name="USER_ID")
	private Map<User, PermissionCollection> userPermissions = new HashMap<User, PermissionCollection>();

	@OneToMany
//	@CollectionTable(name = "ENTITY_GROUPPERMISSIONS", joinColumns = @JoinColumn(name = "ENTITY_ID"))
//	@JoinTable(joinColumns = @JoinColumn(name = "ENTITY_ID"))
	@Cascade(CascadeType.ALL)
	@JsonIgnore
//	@MapKeyJoinColumn(name="GROUP_ID")
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

}
