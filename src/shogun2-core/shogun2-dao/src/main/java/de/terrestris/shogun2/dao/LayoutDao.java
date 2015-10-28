package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.Layout;

@Repository
public class LayoutDao extends GenericHibernateDao<Layout, Integer> {

	protected LayoutDao() {
		super(Layout.class);
	}

}
