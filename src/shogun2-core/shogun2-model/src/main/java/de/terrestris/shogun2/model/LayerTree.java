package de.terrestris.shogun2.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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

	/**
	 * @see java.lang.Object#hashCode()
	 * 
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(37, 17).appendSuper(super.hashCode())
				.append(getRootNode()).toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LayerTree))
			return false;
		LayerTree other = (LayerTree) obj;

		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(getRootNode(), other.getRootNode()).isEquals();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *      Using Apache Commons String Builder.
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.appendSuper(super.toString())
				.append("rootNode", getRootNode()).toString();
	}

}
