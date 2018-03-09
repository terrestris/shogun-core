package de.terrestris.shogun2.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Plugin extends PersistentObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * A name (or display name)
     */
    private String name;

    /**
     * the class name of the plugin
     **/
    @Column(unique = true, nullable = false)
    private String className;

    /**
     * the xtype of the plugin
     **/
    @Column(unique = true)
    private String xtype;

    /**
     * the JavaScript (JS) code of the plugin
     **/
    @Type(type = "text")
    private String sourceCode;

    /**
     * the Cascading Style Sheets (CSS) of the plugin
     **/
    @Type(type = "text")
    private String styleSheet;

    /**
     * Whether the plugin is a system plugin or not.
     * This can e.g. be used to disallow certain actions like editing of the source code.
     * No such limitation will be enforced from SHOGun itself.
     */
    private Boolean systemPlugin;

    /**
     * A list of assigned {@link PluginUploadFile}s.
     */
    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    @JoinTable(
        joinColumns = {@JoinColumn(name = "PLUGIN_ID")},
        inverseJoinColumns = {@JoinColumn(name = "FILE_ID")}
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
     * @return the systemPlugin
     */
    public Boolean getSystemPlugin() {
        return systemPlugin;
    }

    /**
     * @param systemPlugin the systemPlugin to set
     */
    public void setSystemPlugin(Boolean systemPlugin) {
        this.systemPlugin = systemPlugin;
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
     * <p>
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
            .append(getSourceCode())
            .append(getStyleSheet())
            .append(getSystemPlugin())
            .toHashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * <p>
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
            .append(getSourceCode(), other.getSourceCode())
            .append(getStyleSheet(), other.getStyleSheet())
            .append(getSystemPlugin(), other.getSystemPlugin())
            .isEquals();
    }

    /**
     *
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .appendSuper(super.toString())
            .append("name", name)
            .append("className", className)
            .append("xtype", xtype)
            .append("stylesheet", styleSheet)
            .append("systemPlugin", systemPlugin)
            .toString();
    }
}
