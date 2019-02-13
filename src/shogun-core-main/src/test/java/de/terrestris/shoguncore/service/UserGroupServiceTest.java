package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.UserGroupDao;
import de.terrestris.shoguncore.model.UserGroup;

public class UserGroupServiceTest extends
    PermissionAwareCrudServiceTest<UserGroup, UserGroupDao<UserGroup>, UserGroupService<UserGroup, UserGroupDao<UserGroup>>> {

    /**
     * @throws Exception
     */
    public void setUpImplToTest() throws Exception {
        implToTest = new UserGroup();
    }

    @Override
    protected UserGroupService<UserGroup, UserGroupDao<UserGroup>> getCrudService() {
        return new UserGroupService<UserGroup, UserGroupDao<UserGroup>>();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<UserGroupDao<UserGroup>> getDaoClass() {
        return (Class<UserGroupDao<UserGroup>>) new UserGroupDao<UserGroup>().getClass();
    }

}
