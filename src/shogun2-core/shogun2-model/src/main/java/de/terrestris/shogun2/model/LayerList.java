package de.terrestris.shogun2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A module, providing a simple list of {@link Layer}s.
 */
@Entity
@Table
public class LayerList extends Module {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@OneToMany
	@OrderColumn
	private List<Layer> layers = new ArrayList<Layer>();

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public LayerList() {
	}

	/**
	 * @return the layers
	 */
	public List<Layer> getLayers() {
		return layers;
	}

	/**
	 * @param layers
	 *            the layers to set
	 */
	public void setLayers(List<Layer> layers) {
		this.layers = layers;
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
		return new HashCodeBuilder(19, 7).appendSuper(super.hashCode())
				.toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *      According to http://stackoverflow.com/q/27581 it is recommended to
	 *      use only getter-methods when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LayerList))
			return false;
		LayerList other = (LayerList) obj;

		return new EqualsBuilder().appendSuper(super.equals(other)).isEquals();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 *      Using Apache Commons String Builder.
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.appendSuper(super.toString()).append("layers", getLayers())
				.toString();
	}

}
