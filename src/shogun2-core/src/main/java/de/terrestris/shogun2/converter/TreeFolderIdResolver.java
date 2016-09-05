package de.terrestris.shogun2.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.dao.TreeFolderDao;
import de.terrestris.shogun2.model.tree.TreeFolder;
import de.terrestris.shogun2.service.TreeFolderService;

/**
 *
 * @author Nils Buehner
 *
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
