package de.terrestris.shoguncore.init;

import de.terrestris.shoguncore.model.PersistentObject;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.service.InitializationService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Class to initialize an initial set of content based on bean definitions.
 *
 * @author Nils Bühner
 */
public class ContentInitializer {

    /**
     * The Logger
     */
    protected static final Logger logger = getLogger(ContentInitializer.class);
    /**
     * Initialization Service to init shogun content like users or default
     * applications.
     */
    @Autowired
    protected InitializationService initService;
    /**
     * Flag symbolizing if content initialization should be active on startup
     */
    @Autowired
    @Qualifier("shogunInitEnabled")
    private Boolean shogunInitEnabled;
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
            logger.info("Initializing SHOGun content");
            for (PersistentObject object : objectsToCreate) {
                if (object instanceof User) {
                    // special handling of users to encrypt the password!
                    initService.saveUser((User) object);
                } else {
                    initService.savePersistentObject(object);
                }
            }
        } else {
            logger.info("Not initializing anything for SHOGun.");
        }
    }

    /**
     * @param shogunInitEnabled
     */
    public void setShogunInitEnabled(Boolean shogunInitEnabled) {
        this.shogunInitEnabled = shogunInitEnabled;
    }

    /**
     * @param initService
     */
    public void setInitService(InitializationService initService) {
        this.initService = initService;
    }

    /**
     * @param objectsToCreate
     */
    public void setObjectsToCreate(List<PersistentObject> objectsToCreate) {
        this.objectsToCreate = objectsToCreate;
    }
}
