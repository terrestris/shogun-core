package de.terrestris.shogun2.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Plugin extends PersistentObject {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** A name (or display name) */
	private String name;

	/** the class name of the plugin **/
	@Column(unique = true)
	private String className;

	/** the xtype of the plugin **/
	@Column(unique = true)
	private String xtype;

	/** the JavaScript (JS) code of the plugin **/
	@Column(length = Integer.MAX_VALUE)
	private String sourceCode;

	/** the Cascading Style Sheets (CSS) of the plugin **/
	@Column(length = Integer.MAX_VALUE)
	private String styleSheet;

	/** A list of assigned {@link PluginUploadFile}s. */
	@ManyToMany
	@JoinTable(
		joinColumns = { @JoinColumn(name = "PLUGIN_ID") },
		inverseJoinColumns = { @JoinColumn(name = "FILE_ID") }
	)
	@JsonIgnore
	private Set<File> fileUploads = new HashSet<File>();

	/**
	 * Constructor
	 */
	public Plugin() {
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
	 * @return the className
	 */
	@Column(unique = true, nullable = false)
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the xtype
	 */
	@Column(unique = true, nullable = false)
	public String getXtype() {
		return xtype;
	}

	/**
	 * @param xtype the xtype to set
	 */
	public void setXtype(String xtype) {
		this.xtype = xtype;
	}

	/**
	 * @return the sourceCode
	 */
	public String getSourceCode() {
		return sourceCode;
	}

	/**
	 * @param sourceCode the sourceCode to set
	 */
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	/**
	 * @return the styleSheet
	 */
	public String getStyleSheet() {
		return styleSheet;
	}

	/**
	 * @param styleSheet the styleSheet to set
	 */
	public void setStyleSheet(String styleSheet) {
		this.styleSheet = styleSheet;
	}

	/**
	 * @return the fileUploads
	 */
	public Set<File> getFileUploads() {
		return fileUploads;
	}

	/**
	 * @param fileUploads the fileUploads to set
	 */
	public void setFileUploads(Set<File> fileUploads) {
		this.fileUploads = fileUploads;
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
		return new HashCodeBuilder(47, 19) // two randomly chosen prime numbers
			.appendSuper(super.hashCode())
			.append(getName())
			.append(getClassName())
			.append(getXtype())
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
		if (!(obj instanceof Plugin))
			return false;
		Plugin other = (Plugin) obj;

		return new EqualsBuilder()
			.appendSuper(super.equals(other))
			.append(getName(), other.getName())
			.append(getClassName(), other.getClassName())
			.append(getXtype(), other.getXtype())
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
			.append("className", className)
			.append("xtype", xtype)
			.toString();
	}
}
