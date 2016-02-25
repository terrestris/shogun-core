package de.terrestris.shogun2.dao;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.layer.AbstractLayer;

@Repository("abstractLayerDao")
public class AbstractLayerDao<E extends AbstractLayer> extends
		GenericHibernateDao<E, Integer> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public AbstractLayerDao() {
		super((Class<E>) AbstractLayer.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected AbstractLayerDao(Class<E> clazz) {
		super(clazz);
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public Set<E> findLayerGroupsOfAbstractLayer(AbstractLayer abstractLayer) throws HibernateException {
		Criteria criteria = createDistinctRootEntityCriteria();

		criteria.createAlias("layers", "lyr");
		criteria.add(Restrictions.eq("lyr.id", abstractLayer.getId()));

		return new HashSet<E>(criteria.list());
	}
}
