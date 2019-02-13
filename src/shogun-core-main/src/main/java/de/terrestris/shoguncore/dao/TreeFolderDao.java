package de.terrestris.shoguncore.dao;

import org.springframework.stereotype.Repository;

import de.terrestris.shoguncore.model.tree.TreeFolder;

@Repository("treeFolderDao")
public class TreeFolderDao<E extends TreeFolder> extends
    GenericHibernateDao<E, Integer> {

    /**
     * Public default constructor for this DAO.
     */
    @SuppressWarnings("unchecked")
    public TreeFolderDao() {
        super((Class<E>) TreeFolder.class);
    }

    /**
     * Constructor that has to be called by subclasses.
     *
     * @param clazz
     */
    protected TreeFolderDao(Class<E> clazz) {
        super(clazz);
    }

}
