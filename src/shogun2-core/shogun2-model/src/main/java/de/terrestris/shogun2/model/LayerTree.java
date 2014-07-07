package de.terrestris.shogun2.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 */
@Entity
@Table
public class LayerTree extends Module {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@ManyToOne(optional = false)
	private LayerTreeNode rootNode;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public LayerTree() {
	}

	/**
	 * @return the rootNode
	 */
	public LayerTreeNode getRootNode() {
		return rootNode;
	}

	/**
	 * @param rootNode
	 *            the rootNode to set
	 */
	public void setRootNode(LayerTreeNode rootNode) {
		this.rootNode = rootNode;
	}

}
