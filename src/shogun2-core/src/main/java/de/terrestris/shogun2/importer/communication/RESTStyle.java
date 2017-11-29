package de.terrestris.shogun2.importer.communication;

import de.terrestris.shogun2.importer.communication.AbstractRESTEntity;

/**
 *
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 */
public class RESTStyle extends AbstractRESTEntity {

	/**
	 *
	 */
	private String name;

	/**
	 *
	 */
	private String href;

	/**
	 * Default constructor.
	 */
	public RESTStyle() {

	}

	/**
	 *
	 * @param name
	 * @param href
	 */
	public RESTStyle(String name, String href) {
		this.name = name;
		this.href = href;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * @param href the href to set
	 */
	public void setHref(String href) {
		this.href = href;
	}

}
