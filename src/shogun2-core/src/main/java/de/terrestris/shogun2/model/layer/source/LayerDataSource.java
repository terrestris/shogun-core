package de.terrestris.shogun2.model.layer.source;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.terrestris.shogun2.model.PersistentObject;

/**
 *
 * Base class for all layer datasources
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class LayerDataSource extends PersistentObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String type;
	private String url;

	/**
	 * default constructor
	 */
	public LayerDataSource(){
		super();
	}

	/**
	 * @param name
	 * @param type
	 * @param url
	 */
	public LayerDataSource(String name, String type, String url) {
		super();
		this.name = name;
		this.type = type;
		this.url = url;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
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
		return new HashCodeBuilder(37, 13).
				appendSuper(super.hashCode()).
				append(getName()).
				append(getType()).
				append(getUrl()).
				toHashCode();
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
		if (!(obj instanceof LayerDataSource))
			return false;
		LayerDataSource other = (LayerDataSource) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getName(), other.getName()).
				append(getType(), other.getType()).
				append(getUrl(), other.getUrl()).
				isEquals();
	}

}
