/**
 * 
 */
package de.terrestris.shogun2.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 * This class represents the layout of a {@link Module} in a GUI. It provides
 * {@link #propertyHints}, which are (names) of <b>recommended</b> properties of
 * the corresponding {@link Module} and {@link #propertyMusts}, which are
 * (names) of <b>required</b> properties of the {@link Module}. The values of
 * such properties should be stored in
 * {@link Module#setProperties(java.util.Map)}
 * 
 * @author Nils Bühner
 *
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
public class Layout extends PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String type;

	/**
	 * 
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "LAYOUT_PROPERTYHINTS", joinColumns = @JoinColumn(name = "LAYOUT_ID") )
	@Column(name = "PROPERTYNAME")
	private Set<String> propertyHints = new HashSet<String>();

	/**
	 * 
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "LAYOUT_PROPERTYMUSTS", joinColumns = @JoinColumn(name = "LAYOUT_ID") )
	@Column(name = "PROPERTYNAME")
	@OrderColumn(name = "INDEX")
	private Set<String> propertyMusts = new HashSet<String>();

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public Layout() {
	}

	/**
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the propertyHints
	 */
	public Set<String> getPropertyHints() {
		return propertyHints;
	}

	/**
	 * @param propertyHints
	 *            the propertyHints to set
	 */
	public void setPropertyHints(Set<String> propertyHints) {
		this.propertyHints = propertyHints;
	}

	/**
	 * @return the propertyMusts
	 */
	public Set<String> getPropertyMusts() {
		return propertyMusts;
	}

	/**
	 * @param propertyMusts
	 *            the propertyMusts to set
	 */
	public void setPropertyMusts(Set<String> propertyMusts) {
		this.propertyMusts = propertyMusts;
	}

}
