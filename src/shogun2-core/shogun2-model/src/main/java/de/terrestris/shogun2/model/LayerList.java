package de.terrestris.shogun2.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 
 */
@Entity
@Table
public class LayerList extends Module {

	private static final long serialVersionUID = 1L;

	@OneToMany
	@OrderColumn
	private List<Layer> layers;

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

}
