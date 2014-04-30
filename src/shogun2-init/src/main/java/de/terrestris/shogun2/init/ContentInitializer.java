package de.terrestris.shogun2.init;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.service.ApplicationService;

/**
 * @author Nils Bühner
 *
 *         Class to initialize some kind of content
 *
 */
@Transactional
public class ContentInitializer {

	/**
	 * The Logger
	 */
	private static Logger LOG = Logger.getLogger(ContentInitializer.class);

	/**
	 * Flag symbolizing if content initialization should be active on startup
	 */
	@Autowired
	@Qualifier("shogunInitEnabled")
	private Boolean shogunInitEnabled;

	@Autowired
	private ApplicationService applicationService;

	/**
	 * The method called on initialization
	 */
	public void initializeDatabaseContent() {

		if (this.shogunInitEnabled.equals(true)) {
			LOG.info("Initializing some SHOGun content!");
		} else {
			LOG.info("Not initializing anything for SHOGun.");
		}
	}
}