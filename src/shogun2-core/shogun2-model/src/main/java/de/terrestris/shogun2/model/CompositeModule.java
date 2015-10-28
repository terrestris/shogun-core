/**
 * 
 */
package de.terrestris.shogun2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * This (abstract) class represents a composite {@link Module}, i.e. a module
 * having children/submodules.
 * 
 * @author Nils Bühner
 *
 */
@Entity
public abstract class CompositeModule extends Module {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "MODULE_SUBMODULE", joinColumns = { @JoinColumn(name = "MODULE_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "SUBMODULE_ID") })
	@OrderColumn(name = "INDEX")
	private List<Module> subModules = new ArrayList<Module>();

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public CompositeModule() {
	}

	/**
	 * 
	 * @param module
	 */
	public void addModule(Module module) {
		this.subModules.add(module);
	}

	/**
	 * 
	 * @param module
	 */
	public void remove(Module module) {
		this.subModules.remove(module);
	}

	/**
	 * @return the subModules
	 */
	public List<Module> getSubModules() {
		return subModules;
	}

	/**
	 * @param subModules
	 *            the subModules to set
	 */
	public void setSubModules(List<Module> subModules) {
		this.subModules = subModules;
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
		return new HashCodeBuilder(17, 19).appendSuper(super.hashCode()).append(getSubModules()).toHashCode();
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
		if (!(obj instanceof CompositeModule))
			return false;
		CompositeModule other = (CompositeModule) obj;

		return new EqualsBuilder().appendSuper(super.equals(other)).append(getSubModules(), other.getSubModules())
				.isEquals();
	}

	/**
	 *
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).appendSuper(super.toString())
				.append("subModules", getSubModules()).toString();
	}
}
