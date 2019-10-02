package de.terrestris.shoguncore.converter;

import de.terrestris.shoguncore.dao.UserGroupDao;
import de.terrestris.shoguncore.model.UserGroup;
import de.terrestris.shoguncore.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author Nils Buehner
 */
public class UserGroupIdResolver<E extends UserGroup, D extends UserGroupDao<E>, S extends UserGroupService<E, D>> extends
    PersistentObjectIdResolver<E, D, S> {

    @Override
    @Autowired
    @Qualifier("userGroupService")
    public void setService(S service) {
        this.service = service;
    }

}
