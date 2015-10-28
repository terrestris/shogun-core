/**
 * 
 */
package de.terrestris.shogun2.model.layout;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.module.Module;

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
		return new HashCodeBuilder(13, 7).appendSuper(super.hashCode()).append(getType()).append(getPropertyHints())
				.append(getPropertyMusts()).toHashCode();
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
		if (!(obj instanceof Layout))
			return false;
		Layout other = (Layout) obj;

		return new EqualsBuilder().appendSuper(super.equals(other)).append(getType(), other.getType())
				.append(getPropertyHints(), other.getPropertyHints())
				.append(getPropertyMusts(), other.getPropertyMusts()).isEquals();
	}

	/**
	 *
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).appendSuper(super.toString())
				.append("type", getType()).append("propertyHints", getPropertyHints())
				.append("propertyMusts", getPropertyMusts()).toString();
	}

}
