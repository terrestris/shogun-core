package de.terrestris.shogun2.converter;

import de.terrestris.shogun2.model.UserGroup;

/**
 *
 * @author Nils Buehner
 *
 */
public class UserGroupIdResolver<E extends UserGroup> extends
		PersistentObjectIdResolver<UserGroup> {

	/**
	 * Default Constructor
	 */
	public UserGroupIdResolver() {
		super(UserGroup.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param entityClass
	 */
	@SuppressWarnings("unchecked")
	protected UserGroupIdResolver(Class<E> entityClass) {
		super((Class<UserGroup>) entityClass);
	}

}
