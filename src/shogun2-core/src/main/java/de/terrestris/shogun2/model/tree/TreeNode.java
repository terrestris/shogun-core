package de.terrestris.shogun2.model.tree;

import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.PersistentObject;


/**
*
* Representation of a layer which consists a corresponding data source
* and an appearance
*
* @author Kai Volland
* @author terrestris GmbH & Co. KG
*
*/
public abstract class TreeNode extends PersistentObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private String label;

	/**
	 *
	 */
	@ManyToOne
	private PersistentObject associatedEntity;

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the associatedEntity
	 */
	public PersistentObject getAssociatedEntity() {
		return associatedEntity;
	}

	/**
	 * @param associatedEntity the associatedEntity to set
	 */
	public void setAssociatedEntity(PersistentObject associatedEntity) {
		this.associatedEntity = associatedEntity;
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
		return new HashCodeBuilder(1, 5).
				appendSuper(super.hashCode()).
				append(getLabel()).
				append(getAssociatedEntity()).
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
		if (!(obj instanceof TreeNode))
			return false;
		TreeNode other = (TreeNode) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getLabel(), other.getLabel()).
				append(getAssociatedEntity(), other.getAssociatedEntity()).
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
