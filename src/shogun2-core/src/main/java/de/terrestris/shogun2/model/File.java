/**
 *
 */
package de.terrestris.shogun2.model;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This class represents a file which is stored as a bytearray in the database
 *
 * @author Johannes Weskamm
 * @author Daniel Koch
 *
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@AssociationOverrides({
	@AssociationOverride(
			name="userPermissions",
			joinTable=@JoinTable(name="FILES_USERPERMISSIONS",
			joinColumns = @JoinColumn(name = "FILE_ID"))),

	@AssociationOverride(
			name="groupPermissions",
			joinTable=@JoinTable(name="FILES_GROUPPERMISSIONS",
			joinColumns = @JoinColumn(name = "FILE_ID")))
})
public class File extends SecuredPersistentObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	private Boolean active;

	/**
	 *
	 */
	private String fileName;

	/**
	 *
	 */
	private String fileType;

	/**
	 *
	 */
	@JsonIgnore
	@Column(length = Integer.MAX_VALUE)
	private byte[] file;

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * @param fileType the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the file
	 */
	public byte[] getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(byte[] file) {
		this.file = file;
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
		return new HashCodeBuilder(523, 7).appendSuper(super.hashCode())
				.append(getActive())
				.append(getFileName())
				.append(getFileType())
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
		if (!(obj instanceof File))
			return false;
		File other = (File) obj;

		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(getActive(), other.getActive())
				.append(getFileName(), other.getFileName())
				.append(getFileType(), other.getFileType())
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
