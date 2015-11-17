/**
 *
 */
package de.terrestris.shogun2.model.module;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.layout.Layout;
import de.terrestris.shogun2.util.converter.PropertyValueConverter;

/**
 * A module is the visual representation of a component in the GUI. A module can
 * be connected to a {@link Layout} and it stores basic properties (like
 * <i>border</i>, <i>height</i> , <i>width</i>, ...).
 *
 * This class is the simple base class of either simple (e.g. {@link LayerTree})
 * or complex ({@link CompositeModule}) subclasses and can thereby considered
 * as a node in a tree structure of (sub-)modules.
 *
 * @author Nils Bühner
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Module extends PersistentObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private String name;

	/**
	 * The class name (e.g. the xtype in ExtJS) of this module.
	 */
	private String xtype;

	/**
	 *
	 */
	@ElementCollection
	@MapKeyColumn(name = "PROPERTY")
	@Column(name = "VALUE")
	@CollectionTable(name = "MODULE_PROPERTIES", joinColumns = @JoinColumn(name = "MODULE_ID") )
	@Convert(converter = PropertyValueConverter.class, attributeName="value")
	private Map<String, Object> properties = new HashMap<String, Object>();

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
	 * @return the xtype
	 */
	public String getXtype() {
		return xtype;
	}

	/**
	 * @param xtype
	 *            the xtype to set
	 */
	public void setXtype(String xtype) {
		this.xtype = xtype;
	}

	/**
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
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
		return new HashCodeBuilder(5, 7)
				.appendSuper(super.hashCode())
				.append(getName())
				.append(getProperties())
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
		if (!(obj instanceof Module))
			return false;
		Module other = (Module) obj;

		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(getName(), other.getName())
				.append(getXtype(), other.getXtype())
				.append(getProperties(), other.getProperties())
				.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
