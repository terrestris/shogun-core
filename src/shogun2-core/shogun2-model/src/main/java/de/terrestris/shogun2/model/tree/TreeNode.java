package de.terrestris.shogun2.model.tree;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * The TreeNode is an abstract class which is the granular part of an tree.
 *
 * @author Kai Volland
 * @author Daniel Koch
 *
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class TreeNode extends PersistentObject{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private String text;

	/**
	 *
	 */
	private boolean checked;

	/**
	 *
	 */
	public TreeNode() {
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
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
		return new HashCodeBuilder(2, 89)
				.appendSuper(super.hashCode())
				.append(getText())
				.append(isChecked())
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
		if (!(obj instanceof TreeNode))
			return false;
		TreeNode other = (TreeNode) obj;

		return new EqualsBuilder()
				.appendSuper(super.equals(other))
				.append(getText(), other.getText())
				.append(isChecked(), other.isChecked())
				.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("text", getText())
				.append("checked", isChecked())
				.toString();
	}
}
