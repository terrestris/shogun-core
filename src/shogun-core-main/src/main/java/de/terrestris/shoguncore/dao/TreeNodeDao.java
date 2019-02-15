package de.terrestris.shoguncore.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shoguncore.model.tree.TreeNode;

@Repository("treeNodeDao")
public class TreeNodeDao<E extends TreeNode> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public TreeNodeDao() {
        super((Class<E>) TreeNode.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected TreeNodeDao(Class<E> clazz) {
        super(clazz);
    }

}
