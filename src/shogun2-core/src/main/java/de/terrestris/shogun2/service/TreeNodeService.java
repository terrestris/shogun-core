package de.terrestris.shogun2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.dao.TreeNodeDao;
import de.terrestris.shogun2.model.module.Module;
import de.terrestris.shogun2.model.tree.TreeNode;

/**
 * Service class for the {@link Module} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 *
 */
@Service("treeNodeService")
public class TreeNodeService<E extends TreeNode, D extends TreeNodeDao<E>> extends
		PermissionAwareCrudService<E, D> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public TreeNodeService() {
		this((Class<E>) TreeNode.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the service.
	 * Subclasses MUST call this constructor.
	 */
	protected TreeNodeService(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct dao here.
	 * Otherwise, spring can not decide which dao has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("treeNodeDao")
	public void setDao(D dao) {
		this.dao = dao;
	}
}
