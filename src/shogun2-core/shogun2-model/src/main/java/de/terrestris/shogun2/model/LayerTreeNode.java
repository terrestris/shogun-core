package de.terrestris.shogun2.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 */
@Entity
@Table
public class LayerTreeNode extends PersistentObject {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@Column
	private boolean isLeaf;

	/**
	 *
	 */
	@Column
	private String displayText;

	/**
	 *
	 */
	@ManyToOne
	private Layer layer;

	/**
	 *
	 */
	@ManyToOne
	private BaseLayerTheme themeOverride;

	/**
	 *
	 */
	@OneToMany
	@OrderColumn
	private List<LayerTreeNode> children;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public LayerTreeNode() {
	}

	/**
	 * @return the isLeaf
	 */
	public boolean isLeaf() {
		return isLeaf;
	}

	/**
	 * @param isLeaf
	 *            the isLeaf to set
	 */
	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	/**
	 * @return the displayText
	 */
	public String getDisplayText() {
		return displayText;
	}

	/**
	 * @param displayText
	 *            the displayText to set
	 */
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	/**
	 * @return the layer
	 */
	public Layer getLayer() {
		return layer;
	}

	/**
	 * @param layer
	 *            the layer to set
	 */
	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	/**
	 * @return the themeOverride
	 */
	public BaseLayerTheme getThemeOverride() {
		return themeOverride;
	}

	/**
	 * @param themeOverride
	 *            the themeOverride to set
	 */
	public void setThemeOverride(BaseLayerTheme themeOverride) {
		this.themeOverride = themeOverride;
	}

	/**
	 * @return the children
	 */
	public List<LayerTreeNode> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<LayerTreeNode> children) {
		this.children = children;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 * 
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(13, 41).appendSuper(super.hashCode())
				.append(getDisplayText()).append(getLayer())
				.append(getThemeOverride()).append(isLeaf()).toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LayerTreeNode))
			return false;
		LayerTreeNode other = (LayerTreeNode) obj;

		return new EqualsBuilder().appendSuper(super.equals(other))
				.append(getDisplayText(), other.getDisplayText())
				.append(getLayer(), other.getLayer())
				.append(getThemeOverride(), other.getThemeOverride())
				.append(isLeaf(), other.isLeaf()).isEquals();
	}
}
