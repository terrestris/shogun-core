package de.terrestris.shogun2.dao;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;

@Repository
public class UserGroupDao extends GenericHibernateDao<UserGroup, Integer> {

	protected UserGroupDao() {
		super(UserGroup.class);
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public Set<UserGroup> findGroupsOfUser(User user) throws HibernateException {
		Criteria criteria = createDistinctRootEntityCriteria();

		criteria.createAlias("members", "m");
		criteria.add(Restrictions.eq("m.id", user.getId()));

		return new HashSet<UserGroup>(criteria.list());
	}

}
