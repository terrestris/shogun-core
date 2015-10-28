/**
 * 
 */
package de.terrestris.shogun2.model;

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
 * This class is the representation of an border layout, where components are
 * anchored in (predefined) regions, which are stored in the {@link #regions}
 * property.
 * 
 * The order of the {@link #regions} should match the order of the corresponding
 * {@link CompositeModule#getSubModules()}.
 * 
 * @author Nils Bühner
 *
 */
@Table
@Entity
public class BorderLayout extends Layout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public BorderLayout() {
		this.setType("border");
	}

	/**
	 * 
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "BORDERLAYOUT_REGIONS", joinColumns = @JoinColumn(name = "LAYOUT_ID") )
	@Column(name = "REGION")
	@OrderColumn(name = "INDEX")
	private List<String> regions = new ArrayList<String>();

	/**
	 * @return the regions
	 */
	public List<String> getRegions() {
		return regions;
	}

	/**
	 * @param regions
	 *            the regions to set
	 */
	public void setRegions(List<String> regions) {
		this.regions = regions;
	}

}
