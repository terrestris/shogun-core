package de.terrestris.shoguncore.helper;

import de.terrestris.shoguncore.model.PersistentObject;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static org.apache.logging.log4j.LogManager.getLogger;

public class IdHelper {

    private static final Logger logger = getLogger(IdHelper.class);

    /**
     * Helper method that uses reflection to set the (inaccessible) id field of
     * the given {@link PersistentObject}.
     *
     * @param persistentObject The object with the inaccessible id field
     * @param id               The id to set
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static final void setIdOnPersistentObject(
        PersistentObject persistentObject, Integer id)
        throws NoSuchFieldException, IllegalAccessException {
        // use reflection to get the inaccessible final field 'id'
        Field idField = PersistentObject.class.getDeclaredField("id");

        AccessController.doPrivileged((PrivilegedAction<PersistentObject>) () -> {
            idField.setAccessible(true);
            try {
                idField.set(persistentObject, id);
            } catch (IllegalAccessException e) {
                logger.error("Could not set ID field for persistent object", e);
            }
            idField.setAccessible(false);
            return null;
        });

    }
}
