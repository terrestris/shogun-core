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
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public abstract class PersistentObject implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	// The column annotation is used by hibernate for the column creation, e.g.
	// to build constraints like nullable
	@Column(updatable = false, nullable = false)
	private final Integer id = null;

	/**
	 * The getter of this property {@link #getCreated()} is annotated with
	 * {@link JsonIgnore}. This way, the annotation can be overwritten in
	 * subclasses.
	 */
	@Column(updatable = false)
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	private final ReadableDateTime created;

	/**
	 * The getter of this property {@link #getModified()} is annotated with
	 * {@link JsonIgnore}. This way, the annotation can be overwritten in
	 * subclasses.
	 */
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

	/**
	 * Ignore the {@link #created} property when de-/serializing.
	 * This can be overwritten in subclasses.
	 * 
	 * @return The date of the creation of the entity.
	 */
	@JsonIgnore
	public ReadableDateTime getCreated() {
		return created;
	}

	/**
	 * Ignore the {@link #modified} property when de-/serializing.
	 * This can be overwritten in subclasses.
	 * 
	 * @return The date of the last modification of the entity.
	 */
	@JsonIgnore
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
		return new HashCodeBuilder(17, 43).
				append(getCreated()).
				append(getModified()).
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
		if (!(obj instanceof PersistentObject))
			return false;
		PersistentObject other = (PersistentObject) obj;

		return new EqualsBuilder().
				append(getCreated(), other.getCreated()).
				append(getModified(), other.getModified()).
				isEquals();
	}

	/**
	 *
	 */
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.append("created", getCreated())
			.append("modified", getModified())
			.toString();
	}

}
