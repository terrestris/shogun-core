#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.init;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.init.ContentInitializer;

public class ProjectContentInitializer extends ContentInitializer {

	/**
	 * The Logger
	 */
	private static final Logger LOG = Logger.getLogger(ProjectContentInitializer.class);

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
