package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.TreeFolderDao;
import de.terrestris.shoguncore.model.tree.TreeFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service class for the {@link TreeFolder} model.
 *
 * @author Nils Bühner
 * @see AbstractCrudService
 */
@Service("treeFolderService")
public class TreeFolderService<E extends TreeFolder, D extends TreeFolderDao<E>> extends
    PermissionAwareCrudService<E, D> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public TreeFolderService() {
        this((Class<E>) TreeFolder.class);
    }

    /**
     * Constructor that sets the concrete entity class for the service.
     * Subclasses MUST call this constructor.
     */
    protected TreeFolderService(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct dao here.
     * Otherwise, spring can not decide which dao has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("treeFolderDao")
    public void setDao(D dao) {
        this.dao = dao;
    }
}
