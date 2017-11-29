package de.terrestris.shogun2.importer.transform;

import de.terrestris.shogun2.importer.communication.AbstractRESTEntity;

/**
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class RESTTransform extends AbstractRESTEntity {

	/**
	 *
	 */
	private String type;

	/**
	 * Default constructor
	 */
	public RESTTransform() {

	}

	/**
	 *
	 * @param type
	 */
	public RESTTransform(String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
