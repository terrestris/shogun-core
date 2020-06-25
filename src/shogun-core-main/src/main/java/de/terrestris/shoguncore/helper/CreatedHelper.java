package de.terrestris.shoguncore.helper;

import de.terrestris.shoguncore.model.PersistentObject;
import org.apache.logging.log4j.Logger;
import org.joda.time.ReadableDateTime;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static org.apache.logging.log4j.LogManager.getLogger;

public class CreatedHelper {

    private static final Logger logger = getLogger(CreatedHelper.class);

    /**
     * Helper method that uses reflection to set the (inaccessible) created field of
     * the given {@link PersistentObject}.
     *
     * @param persistentObject The object with the inaccessible created field
     * @param created               The created datetime to set
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static final void setCreatedOnPersistentObject(
        PersistentObject persistentObject, ReadableDateTime created)
        throws NoSuchFieldException, IllegalAccessException {
        // use reflection to get the inaccessible final field 'created'
        Field createdField = PersistentObject.class.getDeclaredField("created");

        AccessController.doPrivileged((PrivilegedAction<PersistentObject>) () -> {
            createdField.setAccessible(true);
            try {
                createdField.set(persistentObject, created);
            } catch (IllegalAccessException e) {
                logger.error("Could not set CREATED field for persistent object", e);
            }
            createdField.setAccessible(false);
            return null;
        });

    }
}
