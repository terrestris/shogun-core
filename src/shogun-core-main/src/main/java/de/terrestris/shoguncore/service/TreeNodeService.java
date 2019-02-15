package de.terrestris.shoguncore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.terrestris.shoguncore.dao.TreeNodeDao;
import de.terrestris.shoguncore.helper.IdHelper;
import de.terrestris.shoguncore.model.tree.TreeFolder;
import de.terrestris.shoguncore.model.tree.TreeNode;

/**
 * Service class for the {@link TreeNode} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
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
     * This unproxies and eagerly fetches the whole node/tree and detaches it
     * from the hibernate session before persisting a new "clone" instance in the database.
     * In case of a TreeFolder, each child node will also be re-persisted (as a new database entry).
     *
     * @param node
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    @SuppressWarnings("unchecked")
    public E cloneAndPersistTreeNode(E node) throws Exception {

        if (node == null) {
            throw new Exception("Node to clone must not be null.");
        }

        // unproxy the node to be sure that everything (on top level) is loaded eagerly
        // before we continue to care about possible child notes and persistance
        node = dao.unproxy(node);

        if (node instanceof TreeFolder) {
            List<E> children = (List<E>) ((TreeFolder) node).getChildren();
            List<E> clonedChildren = new ArrayList<>();
            for (E childNode : children) {
                // recursive call for all children
                clonedChildren.add(cloneAndPersistTreeNode(childNode));
            }
            ((TreeFolder) node).setChildren((List<TreeNode>) children);
        }

        // detach the clone from the hibernate session to persist it as a new instance in the next step
        dao.evict(node);

        // set id to null to persist a new instance afterwards
        IdHelper.setIdOnPersistentObject(node, null);

        // persist the same object as a new entry in database
        dao.saveOrUpdate(node);

        return node;
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
