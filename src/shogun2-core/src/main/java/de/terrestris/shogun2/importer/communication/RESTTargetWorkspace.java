package de.terrestris.shogun2.importer.communication;

import de.terrestris.shogun2.importer.communication.AbstractRESTEntity;
import de.terrestris.shogun2.importer.communication.RESTWorkspace;

/**
 * 
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class RESTTargetWorkspace extends AbstractRESTEntity {

	/**
	 * 
	 */
	private RESTWorkspace workspace;

	/**
	 * Default constructor.
	 */
	public RESTTargetWorkspace() {

	}

	/**
	 * 
	 * @param name
	 */
	public RESTTargetWorkspace(String name) {
		this.workspace = new RESTWorkspace(name);
	}

	/**
	 * @return the workspace
	 */
	public RESTWorkspace getWorkspace() {
		return workspace;
	}

	/**
	 * @param workspace the workspace to set
	 */
	public void setWorkspace(RESTWorkspace workspace) {
		this.workspace = workspace;
	}

}
