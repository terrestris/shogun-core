package de.terrestris.shogun2.model.wps;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 */
@Entity
@Table
public class WpsProcessExecute extends WpsReference {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private String identifier;

	/**
	 *
	 */
	private String displayName;

	/**
	 *
	 */
	@ManyToMany
	@JoinTable(
			name = "WPSPROCESSEXECUTES_INPUTS",
			joinColumns = { @JoinColumn(name = "WPSEXECUTEPROCESS_ID") },
			inverseJoinColumns = { @JoinColumn(name = "WPSPARAMETER_ID") }
		)
	@MapKeyColumn(name="IDENTIFIER")
	private Map<String, WpsParameter> input = new HashMap<>();

	/**
	 * Constructor
	 */
	public WpsProcessExecute() {
	}


	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}


	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}


	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	/**
	 * @return the input
	 */
	public Map<String, WpsParameter> getInput() {
		return input;
	}


	/**
	 * @param input the input to set
	 */
	public void setInput(Map<String, WpsParameter> input) {
		this.input = input;
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
		return new HashCodeBuilder(47, 23) // two randomly chosen prime numbers
			.appendSuper(super.hashCode())
			.append(getIdentifier())
			.append(getDisplayName())
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
		if (!(obj instanceof WpsProcessExecute))
			return false;
		WpsProcessExecute other = (WpsProcessExecute) obj;

		return new EqualsBuilder()
			.appendSuper(super.equals(other))
			.append(getIdentifier(), other.getIdentifier())
			.append(getDisplayName(), other.getDisplayName())
			.isEquals();
	}

	/**
	 *
	 */
	@Override
	public String toString(){
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.appendSuper(super.toString())
			.append("identifier", identifier)
			.append("displayName", displayName)
			.toString();
	}
}
