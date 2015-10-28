package de.terrestris.shogun2.model.layer;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.model.layer.appearance.LayerAppearance;
import de.terrestris.shogun2.model.layer.source.LayerDataSource;

/**
 * 
 * Representation of a layer which consists a corresponding data source
 * and an appearance
 * 
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Entity
@Table
public class Layer extends PersistentObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7658400534257889743L;
	private String name;
	private String type;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private LayerDataSource source;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private LayerAppearance appearance;
	
	/**
	 * 
	 */
	public Layer() {
		super();
	}

	/**
	 * @param name Layer name
	 * @param type Layer type
	 * @param source The data source of the layer
	 * @param appearance The appearance configuration of the layer
	 */
	public Layer(String name, String type, LayerDataSource source, LayerAppearance appearance) {
		super();
		this.name = name;
		this.type = type;
		this.source = source;
		this.appearance = appearance;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the source
	 */
	public LayerDataSource getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(LayerDataSource source) {
		this.source = source;
	}

	/**
	 * @return the appearance
	 */
	public LayerAppearance getAppearance() {
		return appearance;
	}

	/**
	 * @param appearance the appearance to set
	 */
	public void setAppearance(LayerAppearance appearance) {
		this.appearance = appearance;
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
		return new HashCodeBuilder(29, 13).
				appendSuper(super.hashCode()).
				append(getName()).
				append(getType()).
				append(getSource().hashCode()).
				append(getAppearance().hashCode()).
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
		if (!(obj instanceof Layer))
			return false;
		Layer other = (Layer) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getName(), other.getName()).
				append(getType(), other.getType()).
				append(getSource(), other.getSource()).
				append(getAppearance(), other.getAppearance()).
				isEquals();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
