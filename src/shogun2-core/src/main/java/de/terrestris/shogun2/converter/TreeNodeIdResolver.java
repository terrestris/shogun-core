package de.terrestris.shogun2.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.dao.TreeNodeDao;
import de.terrestris.shogun2.model.tree.TreeNode;
import de.terrestris.shogun2.service.TreeNodeService;

/**
 *
 * @author Nils Buehner
 *
 */
public class TreeNodeIdResolver<E extends TreeNode, D extends TreeNodeDao<E>, S extends TreeNodeService<E, D>> extends
		PersistentObjectIdResolver<E, D, S> {

	@Override
	@Autowired
	@Qualifier("treeNodeService")
	public void setService(S service) {
		this.service = service;
	}

}
