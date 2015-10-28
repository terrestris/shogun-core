package de.terrestris.shogun2.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.Module;

@Repository
public class ModuleDao extends GenericHibernateDao<Module, Integer> {

	protected ModuleDao() {
		super(Module.class);
	}

}
