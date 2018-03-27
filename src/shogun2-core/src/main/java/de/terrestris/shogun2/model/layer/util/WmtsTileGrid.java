package de.terrestris.shogun2.model.layer.util;

import de.terrestris.shogun2.model.layer.util.TileGrid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Class representing a WMTS tile grid
 *
 * @author Andre Henn
 */
@Entity
@Table
public class WmtsTileGrid extends TileGrid {

	// String array holding the matrix IDs.
	private String[] matrixIds;

	/**
	 *
	 * @return the matrix IDs.
	 */
	public String[] getMatrixIds() {
		return matrixIds;
	}

	/**
	 * Setter for matrix IDs
	 * @param matrixIds The matrix IDs to set
	 */
	public void setMatrixIds(String[] matrixIds) {
		this.matrixIds = matrixIds;
	}

	@Override
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(89, 29).
				appendSuper(super.hashCode()).
				append(getMatrixIds()).
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
		if (!(obj instanceof WmtsTileGrid))
			return false;
		WmtsTileGrid other = (WmtsTileGrid) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getMatrixIds(), other.getMatrixIds()).
				isEquals();
	}

}
