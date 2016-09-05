package de.terrestris.shogun2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.TreeNodeDao;
import de.terrestris.shogun2.model.tree.TreeNode;
import de.terrestris.shogun2.service.TreeNodeService;

/**
 * @author Kai Volland
 * @author Nils Bühner
 *
 */
@RestController
@RequestMapping("/treenodes")
public class TreeNodeRestController<E extends TreeNode, D extends TreeNodeDao<E>, S extends TreeNodeService<E, D>>
		extends AbstractRestController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public TreeNodeRestController() {
		this((Class<E>) TreeNode.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected TreeNodeRestController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("treeNodeService")
	public void setService(S service) {
		this.service = service;
	}
}
