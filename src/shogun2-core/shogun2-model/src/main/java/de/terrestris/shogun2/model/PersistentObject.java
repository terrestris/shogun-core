/**
 * Inspired by
 * http://ocpsoft.org/java/hibernate-use-a-base-class-to-map-common-fields/
 */
package de.terrestris.shogun2.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

/**
 * This class represents the abstract superclass for all entities that are
 * persisted in the database.
 *
 * Subclasses of this class can further be inherited and there should be no
 * problems with hibernate mappings/database interactions.
 *
 * @author Nils Bühner
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PersistentObject implements Serializable {

	private static final long serialVersionUID = 8299954175165022499L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(updatable = false, nullable = false)
	private final Integer id = null;

	@Column(updatable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private final ReadableDateTime created;

	@Column
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private ReadableDateTime modified;

	/**
	 * Constructor
	 */
	protected PersistentObject() {
		this.created = DateTime.now();
		this.modified = DateTime.now();
	}

	public Integer getId() {
		return id;
	}

	public ReadableDateTime getCreated() {
		return created;
	}

	public ReadableDateTime getModified() {
		return modified;
	}

	public void setModified(ReadableDateTime modified) {
		this.modified = modified;
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
		return new HashCodeBuilder(17, 43).appendSuper(super.hashCode())
				.append(getCreated()).append(getModified()).toHashCode();
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
		if (!(obj instanceof PersistentObject))
			return false;
		PersistentObject other = (PersistentObject) obj;

		return new EqualsBuilder().append(getCreated(), other.getCreated())
				.append(getModified(), other.getModified()).isEquals();
	}

	/**
	 *
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.appendSuper(super.toString()).append("id", getId())
				.append("created", getCreated())
				.append("modified", getModified()).toString();
	}

}
