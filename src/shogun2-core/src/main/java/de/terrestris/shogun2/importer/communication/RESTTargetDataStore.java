package de.terrestris.shogun2.importer.communication;

import de.terrestris.shogun2.importer.communication.AbstractRESTEntity;
import de.terrestris.shogun2.importer.communication.RESTDataStore;

/**
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class RESTTargetDataStore extends AbstractRESTEntity {

	/**
	 *
	 */
	private RESTDataStore dataStore;

	/**
	 * Default constructor.
	 */
	public RESTTargetDataStore() {

	}

	/**
	 *
	 * @param name
	 * @param type
	 */
	public RESTTargetDataStore(String name, String type) {
		this.dataStore = new RESTDataStore(name, type);
	}

	/**
	 * @return the dataStore
	 */
	public RESTDataStore getDataStore() {
		return dataStore;
	}

	/**
	 * @param dataStore the dataStore to set
	 */
	public void setDataStore(RESTDataStore dataStore) {
		this.dataStore = dataStore;
	}

}
