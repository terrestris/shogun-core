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
}
