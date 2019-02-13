#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shoguncore.init.ContentInitializer;

/**
 * This is a demo class that demonstrates how the SHOGun-Core
 * {@link ContentInitializer} can be extended.
 *
 * @author Nils Bühner
 *
 */
public class ProjectContentInitializer extends ContentInitializer {

	/**
	 * Flag symbolizing if something should be initialized on startup
	 */
	@Autowired
	@Qualifier("projectInitEnabled")
	private Boolean projectInitEnabled;

	@Override
	public void initializeDatabaseContent() {

		super.initializeDatabaseContent();

		if(projectInitEnabled){
			// init project specific stuff...
			LOG.info("Initializing project specific stuff...");
		}

	}

}
