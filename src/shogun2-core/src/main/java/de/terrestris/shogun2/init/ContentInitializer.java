package de.terrestris.shogun2.init;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.service.InitializationService;

/**
 * Class to initialize an initial set of content based on bean definitions.
 *
 * @author Nils Bühner
 */
public class ContentInitializer {

    /**
     * The Logger
     */
    protected final Logger LOG = Logger.getLogger(getClass());

    /**
     * Flag symbolizing if content initialization should be active on startup
     */
    @Autowired
    @Qualifier("shogunInitEnabled")
    private Boolean shogunInitEnabled;

    /**
     * Initialization Service to init shogun content like users or default
     * applications.
     */
    @Autowired
    protected InitializationService initService;

    /**
     * The list of objects that shall be persisted.
     * <p>
     * The order of the objects is important here (as one object may require
     * that another object has been created before to avoid errors like 'object
     * references an unsaved transient instance save the transient instance
     * before flushing').
     */
    @Resource
    @Qualifier("objectsToCreate")
    private List<PersistentObject> objectsToCreate;

    /**
     * The method called on initialization
     */
    public void initializeDatabaseContent() {

        if (this.shogunInitEnabled) {

            LOG.info("Initializing SHOGun2 content");

            for (PersistentObject object : objectsToCreate) {
                if (object instanceof User) {
                    // special handling of users to encrypt the password!
                    initService.saveUser((User) object);
                } else {
                    initService.savePersistentObject(object);
                }
            }

        } else {
            LOG.info("Not initializing anything for SHOGun2.");
        }
    }

}
