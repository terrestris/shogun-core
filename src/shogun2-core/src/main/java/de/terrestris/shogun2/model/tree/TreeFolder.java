/**
 *
 */
package de.terrestris.shogun2.model.tree;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This class represents a (simple) composite {@link TreeNode}, i.e. a folder
 * having {@link TreeNode}-children.
 *
 * @author Nils Bühner
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class TreeFolder extends TreeNode {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This is NOT (!) the owning side of the parent/child relation, i.e. if you
	 * add a child to this list and persist the instance of this folder, this
	 * will NOT be persisted in the database! You will always have to change the
	 * child nodes itself to persist such changes, e.g. by setting a different
	 * index (for ordering in a folder) or setting a different parentFolder
	 * there.
	 */
	@OneToMany(mappedBy = "parentFolder")
	@OrderBy("index")
	private List<TreeNode> children = new ArrayList<TreeNode>();

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public TreeFolder() {
		super();

		// folders are not leafs...
		this.setLeaf(false);

		// folders are usually expandable
		this.setExpandable(true);
	}

	/**
	 * @return the children
	 */
	public List<TreeNode> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(19, 17)
				.appendSuper(super.hashCode())
				.append(getChildren())
				.toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TreeFolder))
			return false;
		TreeFolder other = (TreeFolder) obj;

		return new EqualsBuilder()
				.appendSuper(super.equals(other))
				.append(getChildren(), other.getChildren())
				.isEquals();
	}
}
