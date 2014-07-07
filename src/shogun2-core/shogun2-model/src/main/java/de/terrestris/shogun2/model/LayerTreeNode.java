package de.terrestris.shogun2.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

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

}
