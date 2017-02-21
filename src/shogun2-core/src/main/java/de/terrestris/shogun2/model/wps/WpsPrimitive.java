package de.terrestris.shogun2.model.wps;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.Plugin;

/**
 *
 */
@Entity
@Table
public class WpsPrimitive extends WpsParameter {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private String defaultTextValue;

	/**
	 *
	 */
	@ManyToOne
	private Plugin inputPlugin;

	/**
	 * Constructor
	 */
	public WpsPrimitive() {
	}

	/**
	 * @return the defaultTextValue
	 */
	public String getDefaultTextValue() {
		return defaultTextValue;
	}

	/**
	 * @param defaultTextValue the defaultTextValue to set
	 */
	public void setDefaultTextValue(String defaultTextValue) {
		this.defaultTextValue = defaultTextValue;
	}

	/**
	 * @return the inputPlugin
	 */
	public Plugin getInputPlugin() {
		return inputPlugin;
	}

	/**
	 * @param inputPlugin the inputPlugin to set
	 */
	public void setInputPlugin(Plugin inputPlugin) {
		this.inputPlugin = inputPlugin;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 *
	 * According to
	 * http://stackoverflow.com/questions/27581/overriding-equals-and-hashcode-in-java
	 * it is recommended only to use getter-methods when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 47) // two randomly chosen prime numbers
			.appendSuper(super.hashCode())
			.append(getDefaultTextValue())
			.append(getInputPlugin())
			.toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 * According to
	 * http://stackoverflow.com/questions/27581/overriding-equals-and-hashcode-in-java
	 * it is recommended only to use getter-methods when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WpsPrimitive))
			return false;
		WpsPrimitive other = (WpsPrimitive) obj;

		return new EqualsBuilder()
			.appendSuper(super.equals(other))
			.append(getDefaultTextValue(), other.getDefaultTextValue())
			.append(getInputPlugin(), other.getInputPlugin())
			.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString(){
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.appendSuper(super.toString())
			.append("defaultTextValue", defaultTextValue)
			.append("inputPlugin", inputPlugin)
			.toString();
	}
}
