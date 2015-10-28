/**
 * 
 */
package de.terrestris.shogun2.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The viewport is the main container representing the viewable application area
 * (i.e. the browser viewport). It is thereby used in an {@link Application}.
 * 
 * @author Nils Bühner
 *
 */
@Table
@Entity
public class Viewport extends CompositeModule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public Viewport() {
	}

}
