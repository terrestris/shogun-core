/**
 *
 */
package de.terrestris.shogun2.model.module;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import de.terrestris.shogun2.model.tree.TreeFolder;

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
	@OneToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private TreeFolder rootNodeConfig;


	/**
	 * @return the rootNodeConfig
	 */
	public TreeFolder getRootNodeConfig() {
		return rootNodeConfig;
	}

	/**
	 * @param rootNodeConfig the rootNodeConfig to set
	 */
	public void setRootNodeConfig(TreeFolder rootNodeConfig) {
		this.rootNodeConfig = rootNodeConfig;
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
		return new HashCodeBuilder(9, 89).
				appendSuper(super.hashCode())
				.append(getRootNodeConfig())
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

		return new EqualsBuilder().
				appendSuper(super.equals(other))
				.append(getRootNodeConfig(), other.getRootNodeConfig())
				.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("treeConfig", getRootNodeConfig())
				.toString();
	}

}
