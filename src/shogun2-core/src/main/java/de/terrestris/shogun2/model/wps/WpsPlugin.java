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
public class WpsPlugin extends Plugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	@ManyToOne
	private WpsProcessExecute process;

	/**
	 * Constructor
	 */
	public WpsPlugin() {
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the process
	 */
	public WpsProcessExecute getProcess() {
		return process;
	}


	/**
	 * @param process the process to set
	 */
	public void setProcess(WpsProcessExecute process) {
		this.process = process;
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
		return new HashCodeBuilder(19, 59) // two randomly chosen prime numbers
			.appendSuper(super.hashCode())
			.append(getName())
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
		if (!(obj instanceof WpsPlugin))
			return false;
		WpsPlugin other = (WpsPlugin) obj;

		return new EqualsBuilder()
			.appendSuper(super.equals(other))
			.append(getName(), other.getName())
			.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString(){
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.appendSuper(super.toString())
			.append("name", name)
			.toString();
	}
}
