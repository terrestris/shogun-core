package de.terrestris.shogun2.dao;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layer.LayerGroup;

@Repository("layerGroupDao")
public class LayerGroupDao<E extends LayerGroup> extends AbstractLayerDao<E> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public LayerGroupDao() {
		super((Class<E>) LayerGroup.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected LayerGroupDao(Class<E> clazz) {
		super(clazz);
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public Set<E> findLayerGroupsOfAbstractLayer(Integer abstractLayerId) throws HibernateException {
		Criteria criteria = createDistinctRootEntityCriteria();

		criteria.createAlias("layers", "lyr");
		criteria.add(Restrictions.eq("lyr.id", abstractLayerId));

		return new HashSet<E>(criteria.list());
	}

}
