/**
 *
 */
package de.terrestris.shogun2.model.module;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This class represents the an Panel with an accordion layout.
 *
 * @author Kai Volland
 *
 */
@Table
@Entity
public class AccordionPanel extends CompositeModule {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Defines which of the contained Modules is initially expanded.
	 */
	private Module expandedItem;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public AccordionPanel() {
	}

	/**
	 * @return the expandedItem
	 */
	public Module getExpandedItem() {
		return expandedItem;
	}

	/**
	 * @param expandedItem the expandedItem to set
	 */
	public void setExpandedItem(Module expandedItem) {
		this.expandedItem = expandedItem;
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
		return new HashCodeBuilder(11, 3).
				appendSuper(super.hashCode()).
				append(getExpandedItem()).
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
	public boolean equals(Object obj) {
		if (!(obj instanceof AccordionPanel))
			return false;
		AccordionPanel other = (AccordionPanel) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getExpandedItem(), other.getExpandedItem()).
				isEquals();
	}

}
