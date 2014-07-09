package de.terrestris.shogun2.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class represents a module, which will usually result in a visual
 * component in a GUI. Possible modules could be a {@link Map} or a
 * {@link LayerTree}.
 * 
 * A module can include a set of further modules (i.e. subModules).
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Module extends PersistentObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@Column
	private String name;

	/**
	 * 
	 */
	@OneToMany
	private Set<Module> subModules = new HashSet<Module>();

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public Module() {
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
	 * @return the subModules
	 */
	public Set<Module> getSubModules() {
		return subModules;
	}

	/**
	 * @param subModules
	 *            the subModules to set
	 */
	public void setSubModules(Set<Module> subModules) {
		this.subModules = subModules;
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
		return new HashCodeBuilder(31, 19).appendSuper(super.hashCode())
				.append(getName()).toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Module))
			return false;
		Module other = (Module) obj;

		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(getName(), other.getName()).isEquals();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *      Using Apache Commons String Builder.
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.appendSuper(super.toString()).append("name", getName())
				.toString();
	}

}
