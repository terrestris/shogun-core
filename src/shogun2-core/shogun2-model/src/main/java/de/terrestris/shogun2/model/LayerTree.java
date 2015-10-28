/**
 * 
 */
package de.terrestris.shogun2.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A LayerTree is a simple module, where layers (of a map) are organized in a
 * flexible tree structure.
 * 
 * @author Nils Bühner
 *
 */
@Entity
@Table
public class LayerTree extends Module {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public LayerTree() {
	}

}
