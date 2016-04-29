package de.terrestris.shogun2.model.tree;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.terrestris.shogun2.model.layer.AbstractLayer;

/**
 * The TreeLeaf extends the TreeNode and has an associated layer.
 *
 * @author Kai Volland
 * @author Daniel Koch
 *
 */
@Entity
public class TreeLeaf extends TreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@ManyToOne
	@JsonIdentityInfo(
			generator=ObjectIdGenerators.PropertyGenerator.class,
			property="id"
	)
	@JsonIdentityReference(alwaysAsId=true)
	private AbstractLayer layer;

	/**
	 * 
	 */
	public TreeLeaf() {
	}

	/**
	 * @return the layer
	 */
	public AbstractLayer getLayer() {
		return layer;
	}

	/**
	 * @param layer the layer to set
	 */
	public void setLayer(AbstractLayer layer) {
		this.layer = layer;
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
		return new HashCodeBuilder(5, 89)
				.appendSuper(super.hashCode())
				.append(getLayer())
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
		if (!(obj instanceof TreeLeaf))
			return false;
		TreeLeaf other = (TreeLeaf) obj;

		return new EqualsBuilder()
				.appendSuper(super.equals(other))
				.append(getLayer(), other.getLayer())
				.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("layer", getLayer())
				.toString();
	}
}
