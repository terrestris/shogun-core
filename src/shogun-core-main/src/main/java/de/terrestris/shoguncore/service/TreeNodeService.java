package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.TreeNodeDao;
import de.terrestris.shoguncore.helper.IdHelper;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.UserGroup;
import de.terrestris.shoguncore.model.security.PermissionCollection;
import de.terrestris.shoguncore.model.tree.TreeFolder;
import de.terrestris.shoguncore.model.tree.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public E cloneAndPersistTreeNode(E node) throws Exception {
        return cloneAndPersistTreeNode(node, true, new ArrayList<>());
    }

    private E cloneAndPersistTreeNode(E node, boolean root, List<E> nodes) throws Exception {
        if (node == null) {
            throw new Exception("Node to clone must not be null.");
        }

        // unproxy the node to be sure that everything (on top level) is loaded eagerly
        // before we continue to care about possible child notes and persistance
        node = dao.unproxy(node);
        HashMap<User, PermissionCollection> userPermissions = new HashMap<>();
        node.getUserPermissions().forEach((user, coll) -> {
            PermissionCollection collection = new PermissionCollection();
            collection.setPermissions(new HashSet<>(coll.getPermissions()));
            userPermissions.put(user, collection);
        });
        node.setUserPermissions(userPermissions);
        HashMap<UserGroup, PermissionCollection> groupPermissions = new HashMap<>();
        node.getGroupPermissions().forEach((group, coll) -> {
            PermissionCollection collection = new PermissionCollection();
            collection.setPermissions(new HashSet<>(coll.getPermissions()));
            groupPermissions.put(group, collection);
        });
        node.setGroupPermissions(groupPermissions);

        if (node instanceof TreeFolder) {
            List<E> children = (List<E>) ((TreeFolder) node).getChildren();
            List<E> clonedChildren = new ArrayList<>();
            for (E childNode : children) {
                // recursive call for all children
                E child = cloneAndPersistTreeNode(childNode, false, nodes);
                child.setParentFolder((TreeFolder) node);
                clonedChildren.add(child);
            }
            ((TreeFolder) node).setChildren((List<TreeNode>) clonedChildren);
        }

        // detach the clone from the hibernate session to persist it as a new instance in the next step
        dao.evict(node);

        // set id to null to persist a new instance afterwards
        IdHelper.setIdOnPersistentObject(node, null);

        nodes.add(node);

        if (root) {
            // persist the same object as a new entry in database
            Collections.reverse(nodes);
            nodes.forEach(dao::saveOrUpdate);
        }

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
