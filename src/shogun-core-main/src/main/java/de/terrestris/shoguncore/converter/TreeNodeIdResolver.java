package de.terrestris.shoguncore.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shoguncore.dao.TreeNodeDao;
import de.terrestris.shoguncore.model.tree.TreeNode;
import de.terrestris.shoguncore.service.TreeNodeService;

/**
 * @author Nils Buehner
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
