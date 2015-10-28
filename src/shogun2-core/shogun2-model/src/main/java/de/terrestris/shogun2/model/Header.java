/**
 * 
 */
package de.terrestris.shogun2.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class represents the header area in a GUI.
 * 
 * @author Nils Bühner
 *
 */
@Table
@Entity
public class Header extends CompositeModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public Header() {
	}

}
