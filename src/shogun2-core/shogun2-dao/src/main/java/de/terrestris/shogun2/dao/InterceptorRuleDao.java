package de.terrestris.shogun2.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.interceptor.InterceptorRule;

/**
 *
 * @author Daniel Koch
 * @author Kai Volland
 * @author terrestris GmbH & Co. KG
 *
 * @param <E>
 */
@Repository("interceptorRuleDao")
public class InterceptorRuleDao<E extends InterceptorRule>
		extends GenericHibernateDao<E, Integer> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public InterceptorRuleDao() {
		super((Class<E>) InterceptorRule.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected InterceptorRuleDao(Class<E> clazz) {
		super(clazz);
	}

	/**
	 *
	 * @param filterMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> findSpecificRule(Map<String, String> filterMap) {

		Criteria criteria = createDistinctRootEntityCriteria();

		for (Entry<String, String> filter : filterMap.entrySet()) {
			if (StringUtils.isNotEmpty(filter.getValue())) {
				criteria.add(Restrictions.eq(filter.getKey(),
						filter.getValue()).ignoreCase());
			} else {
				criteria.add(Restrictions.isNull(filter.getKey()));
			}
		}

		List<E> result = criteria.list();

		return result;
	}

}
