package de.terrestris.shogun2.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.WmsLayerDao;
import de.terrestris.shogun2.model.WmsLayer;

/**
 * @author Nils Bühner
 * 
 */
@Service("wmsLayerService")
@Transactional(readOnly = true)
public class WmsLayerService {

	private static final Logger log = Logger.getLogger(WmsLayerService.class);

	@Autowired
	WmsLayerDao wmsLayerDao;

	@Transactional(readOnly = false)
	public WmsLayer createWmsLayer(WmsLayer wmsLayer) {

		wmsLayerDao.saveOrUpdate(wmsLayer);

		log.info("Saved WmsLayer: " + wmsLayer);

		return wmsLayer;
	}

	public List<WmsLayer> findAllWmsLayers() {
		return wmsLayerDao.findAll();
	}

	public List<WmsLayer> findWmsLayersLike(String name) {
		Criterion criterion = Restrictions.ilike("name", "%" + name + "%");
		return wmsLayerDao.findByCriteria(criterion);
	}

	public WmsLayer findById(Integer id) {
		return wmsLayerDao.findById(id);
	}

}
