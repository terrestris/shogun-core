package de.terrestris.shogun2.dao;

import de.terrestris.shogun2.model.WmsLayer;

/**
 * @author Nils Bühner
 * 
 */
public class WmsLayerDao extends GenericHibernateDao<WmsLayer, Integer> {

	protected WmsLayerDao() {
		super(WmsLayer.class);
	}

}
