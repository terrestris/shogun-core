/**
 * 
 */
package de.terrestris.shogun2.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 * This class is the representation of an absolute layout, where components are
 * anchored in absolute positions, which are stored in the {@link #coords}
 * property.
 * 
 * The order of the {@link #coords} should match the order of the corresponding
 * {@link CompositeModule#getSubModules()}.
 * 
 * @author Nils Bühner
 *
 */
@Table
@Entity
public class AbsoluteLayout extends Layout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public AbsoluteLayout() {
		this.setType("absolute");
	}

	/**
	 * 
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ABSOLUTELAYOUT_COORDS", joinColumns = @JoinColumn(name = "LAYOUT_ID") )
	@Column(name = "COORD")
	@OrderColumn(name = "INDEX")
	private List<Point> coords = new ArrayList<Point>();

	/**
	 * @return the coords
	 */
	public List<Point> getCoords() {
		return coords;
	}

	/**
	 * @param coords
	 *            the coords to set
	 */
	public void setCoords(List<Point> coords) {
		this.coords = coords;
	}

}
