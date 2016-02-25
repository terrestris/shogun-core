package de.terrestris.shogun2.dao;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layer.AbstractLayer;
import de.terrestris.shogun2.model.module.Map;

@Repository("mapDao")
public class MapDao<E extends Map> extends
		ModuleDao<E> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public MapDao() {
		super((Class<E>) Map.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected MapDao(Class<E> clazz) {
		super(clazz);
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public Set<E> findMapsWithLayer(AbstractLayer layer) throws HibernateException {
		Criteria criteria = createDistinctRootEntityCriteria();

		criteria.createAlias("mapLayers", "ml");
		criteria.add(Restrictions.eq("ml.id", layer.getId()));

		return new HashSet<E>(criteria.list());
	}

}
