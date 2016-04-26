package de.terrestris.shogun2.model.security;


/**
 * @author Nils Bühner
 *
 */
public enum Permission {
	ADMIN("ADMIN"),
	CREATE("CREATE"),
	DELETE("DELETE"),
	UPDATE("UPDATE"),
	READ("READ");

	private final String permission;

	/**
	 * Enum constructor
	 *
	 * @param value
	 */
	private Permission(String permission) {
		this.permission = permission;
	}

	public static Permission fromString(String inputValue) {
		if (inputValue != null) {
			for (Permission permission : Permission.values()) {
				if (inputValue.equalsIgnoreCase(permission.permission)) {
					return permission;
				}
			}
		}
		return null;
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return permission;
	}

}
