package de.terrestris.shogun2.model.tree;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.terrestris.shogun2.model.layer.LayerGroup;

/**
 * The TreeFolder extends the TreeNode and contains n TreeNodes as children.
 *
 * @author Kai Volland
 * @author Daniel Koch
 *
 */
@Entity
public class TreeFolder extends TreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private boolean expanded;

	/**
	 * 
	 */
	@ManyToOne
	@JsonIdentityInfo(
			generator=ObjectIdGenerators.PropertyGenerator.class,
			property="id"
	)
	@JsonIdentityReference(alwaysAsId=true)
	private LayerGroup layerGroup;

	/**
	 * 
	 */
	@ManyToMany
	@JoinTable(
		name = "TREEFOLDER_CHILDREN",
		joinColumns = { @JoinColumn(name = "TREEFOLDER_ID") },
		inverseJoinColumns = { @JoinColumn(name = "TREENODE_ID") }
	)
	@OrderColumn(name = "IDX")
	@Cascade(CascadeType.SAVE_UPDATE)
	private List<TreeNode> children = new ArrayList<TreeNode>();

	/**
	 * 
	 */
	public TreeFolder() {
	}

	/**
	 * @return the expanded
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * @param expanded the expanded to set
	 */
	public void setExpanded(boolean expanded) {
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
	 * @return the layerGroup
	 */
	public LayerGroup getLayerGroup() {
		return layerGroup;
	}

	/**
	 * @param layerGroup the layerGroup to set
	 */
	public void setLayerGroup(LayerGroup layerGroup) {
		this.layerGroup = layerGroup;
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
		return new HashCodeBuilder(7, 89)
				.appendSuper(super.hashCode())
				.append(isExpanded())
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
				.append(isExpanded(), other.isExpanded())
				.append(getChildren(), other.getChildren())
				.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("expanded", isExpanded())
				.append("children", getChildren())
				.toString();
	}

}
