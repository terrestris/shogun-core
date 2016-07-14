package de.terrestris.shogun2.model.tree;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
*
* Representation of a layer which consists a corresponding data source
* and an appearance
*
* @author Kai Volland
* @author terrestris GmbH & Co. KG
*
*/
@Entity
@Table
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TreeFolder extends TreeNode {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The initial expand status of the folder
	 */
	private Boolean expanded;

	/**
	 *
	 */
	@ManyToMany
	@JoinTable(
		name = "TREEFOLDER_CHILDREN",
		joinColumns = { @JoinColumn(name = "TREEFOLDER_ID") },
		inverseJoinColumns = { @JoinColumn(name = "CHILDREN_ID") }
	)
	@OrderColumn(name = "IDX")
	private List<TreeNode> children = new ArrayList<TreeNode>();

	/**
	 * @return the expanded
	 */
	public Boolean getExpanded() {
		return expanded;
	}

	/**
	 * @param expanded the expanded to set
	 */
	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

	/**
	 * @return the children
	 */
	public List<TreeNode> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
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
		return new HashCodeBuilder(1, 7).
				appendSuper(super.hashCode()).
				append(getExpanded()).
				append(getChildren()).
				toHashCode();
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

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getExpanded(), other.getExpanded()).
				append(getChildren(), other.getChildren()).
				isEquals();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
