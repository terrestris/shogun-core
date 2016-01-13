package de.terrestris.shogun2.dao;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;

@Repository("userGroupDao")
public class UserGroupDao<E extends UserGroup> extends
		GenericHibernateDao<E, Integer> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public UserGroupDao() {
		super((Class<E>) UserGroup.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected UserGroupDao(Class<E> clazz) {
		super(clazz);
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public Set<E> findGroupsOfUser(User user) throws HibernateException {
		Criteria criteria = createDistinctRootEntityCriteria();

		criteria.createAlias("members", "m");
		criteria.add(Restrictions.eq("m.id", user.getId()));

		return new HashSet<E>(criteria.list());
	}

}
