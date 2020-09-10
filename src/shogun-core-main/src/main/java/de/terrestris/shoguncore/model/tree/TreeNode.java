/**
 *
 */
package de.terrestris.shoguncore.model.tree;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.terrestris.shoguncore.converter.TreeFolderIdResolver;
import de.terrestris.shoguncore.model.PersistentObject;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * A class representing a node in a tree. This class can be used for leaf nodes.
 * <p>
 * For folders, {@link TreeFolder} should be used.
 *
 * @author Nils Bühner
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 */
@Entity
@Table
@Inheritance(strategy = InheritanceType.JOINED)
@JsonInclude(Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TreeNode extends PersistentObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The text to show on node label (html tags are accepted)
     */
    private String text;

    /**
     * This is the owning side of the relation between parent/child!
     */
    @ManyToOne
    @JoinColumn(name = "PARENTFOLDER_ID")
    @JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        resolver = TreeFolderIdResolver.class
    )
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("parentId")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Fetch(FetchMode.JOIN)
    private TreeFolder parentFolder;

    /**
     * The position of the node inside its parent. When parent has 4 children
     * and the node is third amongst them, index will be 2 -&gt; starting with
     * index 0
     */
    private int index;

    /**
     * True if this is the root node.
     */
    private boolean root = false;

    /**
     * Set to true to indicate that this child can have no children. The expand
     * icon/arrow will then not be rendered for this node.
     * <p>
     * As this class is used for leafs, we'll set the leaf property to true by default.
     * {@link TreeFolder}s will set this property to false in the constructor.
     */
    private boolean leaf = true;

    /**
     * Control checkboxes:
     * <p>
     * <ul>
     * <li>null: no checkbox will appear</li>
     * <li>true: a checked checkbox will appear</li>
     * <li>false: an unchecked checkbox will appear</li>
     * </ul>
     */
    private Boolean checked;

    /**
     * False to prevent expanding/collapsing of this node.
     */
    private boolean expandable;

    /**
     * True if the node is expanded.
     */
    private boolean expanded;

    /**
     * Path to an image to use as an icon.
     */
    private String icon;

    /**
     * One or more space separated CSS classes to be applied to the icon
     * element. The CSS rule(s) applied should specify a background image to be
     * used as the icon.
     */
    private String iconCls;

    /**
     * Tooltip text to show on this node.
     */
    private String qTip;

    /**
     * Tooltip title.
     */
    private String qTitle;


    /**
     * Explicitly adding the default constructor as this is important, e.g. for
     * Hibernate: http://goo.gl/3Cr1pw
     */
    public TreeNode() {
    }

    /**
     *
     */
    public TreeNode(String text) {
        this.text = text;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the parentFolder
     */
    public TreeFolder getParentFolder() {
        return parentFolder;
    }

    /**
     * @param parentFolder the parentFolder to set
     */
    public void setParentFolder(TreeFolder parentFolder) {
        this.parentFolder = parentFolder;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the root
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(boolean root) {
        this.root = root;
    }

    /**
     * @return the leaf
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * @param leaf the leaf to set
     */
    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    /**
     * @return the checked
     */
    public Boolean isChecked() {
        return checked;
    }

    /**
     * @param checked the checked to set
     */
    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    /**
     * @return the expandable
     */
    public boolean isExpandable() {
        return expandable;
    }

    /**
     * @param expandable the expandable to set
     */
    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    /**
     * @return the expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * @param expanded the expanded to set
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the iconCls
     */
    public String getIconCls() {
        return iconCls;
    }

    /**
     * @param iconCls the iconCls to set
     */
    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    /**
     * @return the qTip
     */
    public String getqTip() {
        return qTip;
    }

    /**
     * @param qTip the qTip to set
     */
    public void setqTip(String qTip) {
        this.qTip = qTip;
    }

    /**
     * @return the qTitle
     */
    public String getqTitle() {
        return qTitle;
    }

    /**
     * @param qTitle the qTitle to set
     */
    public void setqTitle(String qTitle) {
        this.qTitle = qTitle;
    }

    /**
     * @see java.lang.Object#hashCode()
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
     */
    @Override
    public int hashCode() {
        // two randomly chosen prime numbers
        return new HashCodeBuilder(17, 5).
            appendSuper(super.hashCode()).
            append(getText()).
            append(getIndex()).
            append(isRoot()).
            append(isLeaf()).
            append(isChecked()).
            append(isExpandable()).
            append(isExpanded()).
            append(getIcon()).
            append(getIconCls()).
            append(getqTip()).
            append(getqTitle()).
            toHashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * <p>
     * According to
     * http://stackoverflow.com/questions/27581/overriding-equals
     * -and-hashcode-in-java it is recommended only to use getter-methods
     * when using ORM like Hibernate
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TreeNode)) {
            return false;
        }
        TreeNode other = (TreeNode) obj;

        return new EqualsBuilder().appendSuper(super.equals(other)).
            append(getText(), other.getText()).
            append(getIndex(), other.getIndex()).
            append(isLeaf(), other.isLeaf()).
            append(isRoot(), other.isRoot()).
            append(isChecked(), other.isChecked()).
            append(isExpandable(), other.isExpandable()).
            append(isExpanded(), other.isExpanded()).
            append(getIcon(), other.getIcon()).
            append(getIconCls(), other.getIconCls()).
            append(getqTip(), other.getqTip()).
            append(getqTitle(), other.getqTitle()).
            isEquals();
    }
}
