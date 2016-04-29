/**
 *
 */
package de.terrestris.shogun2.model.module;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import de.terrestris.shogun2.model.tree.TreeNode;

/**
 * The TreePanel module contains a RootNodeConfig which contains a TreeFolder.
 *
 * @author Kai Volland
 * @author Daniel Koch
 *
 */
@Entity
@Table
public class TreePanel extends Module {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@ManyToMany
	@JoinTable(
		joinColumns = { @JoinColumn(name = "TREEPANEL_NODECONFIGS") },
		inverseJoinColumns = { @JoinColumn(name = "NODECONFIG_ID") }
	)
	@OrderColumn(name = "IDX")
	@Cascade(CascadeType.SAVE_UPDATE)
	private List<TreeNode> nodeConfigs = new ArrayList<TreeNode>();

	/**
	 * @return the nodeConfigs
	 */
	public List<TreeNode> getNodeConfigs() {
		return nodeConfigs;
	}

	/**
	 * @param nodeConfigs the nodeConfigs to set
	 */
	public void setNodeConfigs(List<TreeNode> nodeConfigs) {
		this.nodeConfigs = nodeConfigs;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(9, 89)
				.appendSuper(super.hashCode())
				.append(getNodeConfigs())
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
	public boolean equals(Object obj) {
		if (!(obj instanceof TreePanel))
			return false;
		TreePanel other = (TreePanel) obj;

		return new EqualsBuilder()
				.appendSuper(super.equals(other))
				.append(getNodeConfigs(), other.getNodeConfigs())
				.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append(getNodeConfigs())
				.toString();
	}

}
