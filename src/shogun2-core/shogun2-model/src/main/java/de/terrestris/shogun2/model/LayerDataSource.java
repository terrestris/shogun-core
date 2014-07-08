package de.terrestris.shogun2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class LayerDataSource extends PersistentObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@Column
	private String name;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public LayerDataSource() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 * 
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(11, 23).appendSuper(super.hashCode())
				.append(getName()).toHashCode();
	}

}
