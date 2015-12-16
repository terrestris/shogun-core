package de.terrestris.shogun2.util.test;

import java.lang.reflect.Field;

import de.terrestris.shogun2.model.PersistentObject;

public class TestUtil {

	/**
	 * Helper method that uses reflection to set the (inaccessible) id field of
	 * the given {@link PersistentObject}.
	 * 
	 * @param persistentObject
	 *            The object with the inaccessible id field
	 * @param id
	 *            The id to set
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static final void setIdOnPersistentObject(
			PersistentObject persistentObject, Integer id)
			throws NoSuchFieldException, IllegalAccessException {
		// use reflection to get the inaccessible final field 'id'
		Field idField = PersistentObject.class.getDeclaredField("id");

		// make the field accessible and set the value
		idField.setAccessible(true);
		idField.set(persistentObject, id);
		idField.setAccessible(false);
	}
}
