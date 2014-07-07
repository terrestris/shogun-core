package de.terrestris.shogun2.model;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 *
 */
@Entity
@Table
public class Map extends Module {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@ElementCollection
	@OrderColumn
	private List<Double> center;

	/**
	 *
	 */
	@ElementCollection
	@OrderColumn
	private List<Double> bbox;

	/**
	 * Explicitly adding the default constructor as this is important, e.g. for
	 * Hibernate: http://goo.gl/3Cr1pw
	 */
	public Map() {
	}

	/**
	 * @return the center
	 */
	public List<Double> getCenter() {
		return center;
	}

	/**
	 * @param center
	 *            the center to set
	 */
	public void setCenter(List<Double> center) {
		this.center = center;
	}

	/**
	 * @return the bbox
	 */
	public List<Double> getBbox() {
		return bbox;
	}

	/**
	 * @param bbox
	 *            the bbox to set
	 */
	public void setBbox(List<Double> bbox) {
		this.bbox = bbox;
	}

}
