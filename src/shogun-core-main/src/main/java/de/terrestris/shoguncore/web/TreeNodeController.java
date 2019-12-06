package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.dao.TreeNodeDao;
import de.terrestris.shoguncore.model.tree.TreeNode;
import de.terrestris.shoguncore.service.TreeNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Nils Bühner
 */
@Controller
@RequestMapping("/treenodes")
public class TreeNodeController<E extends TreeNode, D extends TreeNodeDao<E>, S extends TreeNodeService<E, D>>
    extends AbstractWebController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public TreeNodeController() {
        this((Class<E>) TreeNode.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected TreeNodeController(Class<E> entityClass) {
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
