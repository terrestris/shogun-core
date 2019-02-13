package de.terrestris.shoguncore.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shoguncore.dao.TreeFolderDao;
import de.terrestris.shoguncore.model.tree.TreeFolder;
import de.terrestris.shoguncore.service.TreeFolderService;

/**
 * @author Nils Buehner
 */
public class TreeFolderIdResolver<E extends TreeFolder, D extends TreeFolderDao<E>, S extends TreeFolderService<E, D>> extends
    PersistentObjectIdResolver<E, D, S> {

    @Override
    @Autowired
    @Qualifier("treeFolderService")
    public void setService(S service) {
        this.service = service;
    }

}
