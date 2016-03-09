package de.terrestris.shogun2.model.layer;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * Representation of a layer which consists a corresponding data source
 * and an appearance
 *
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class LayerGroup extends AbstractLayer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public LayerGroup() {
		super();
		this.setType("Group");
	}

	/**
	 *
	 */
	@ManyToMany
	@JoinTable(
		name = "LAYERGROUP_LAYERS",
		joinColumns = { @JoinColumn(name = "LAYERGROUP_ID") },
		inverseJoinColumns = { @JoinColumn(name = "LAYER_ID") }
	)
	@OrderColumn(name = "IDX")
	private List<AbstractLayer> layers = new ArrayList<AbstractLayer>();

	/**
	 *
	 * @param module
	 */
	public void addLayer(AbstractLayer layer) {
		this.layers.add(layer);
	}

	/**
	 *
	 * @param module
	 */
	public void remove(AbstractLayer layer) {
		this.layers.remove(layer);
	}

	/**
	 * @return the layers
	 */
	public List<AbstractLayer> getLayers() {
		return layers;
	}

	/**
	 * @param layers the layers to set
	 */
	public void setLayers(List<AbstractLayer> layers) {
		this.layers = layers;
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
		return new HashCodeBuilder(29, 13).
				appendSuper(super.hashCode()).
				append(getLayers()).
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
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LayerGroup))
			return false;
		LayerGroup other = (LayerGroup) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getLayers(), other.getLayers()).
				isEquals();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
