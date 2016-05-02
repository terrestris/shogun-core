package de.terrestris.shogun2.model.tree;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.terrestris.shogun2.converter.LayerGroupIdResolver;
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
			property="id",
			resolver = LayerGroupIdResolver.class
	)
	@JsonIdentityReference(alwaysAsId=true)
	private LayerGroup layerGroup;

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
				.append(getLayerGroup())
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
				.append(getLayerGroup(), other.getLayerGroup())
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
				.append("layerGroup", getLayerGroup())
				.toString();
	}

}
