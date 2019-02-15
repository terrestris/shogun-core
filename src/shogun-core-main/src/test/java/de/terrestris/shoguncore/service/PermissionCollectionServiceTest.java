package de.terrestris.shoguncore.service;

import de.terrestris.shoguncore.dao.PermissionCollectionDao;
import de.terrestris.shoguncore.model.security.PermissionCollection;

public class PermissionCollectionServiceTest extends
    AbstractCrudServiceTest<PermissionCollection,
        PermissionCollectionDao<PermissionCollection>,
        PermissionCollectionService<PermissionCollection, PermissionCollectionDao<PermissionCollection>>> {

    /**
     * @throws Exception
     */
    public void setUpImplToTest() throws Exception {
        implToTest = new PermissionCollection();
    }

    @Override
    protected PermissionCollectionService<PermissionCollection, PermissionCollectionDao<PermissionCollection>> getCrudService() {
        return new PermissionCollectionService<PermissionCollection, PermissionCollectionDao<PermissionCollection>>();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<PermissionCollectionDao<PermissionCollection>> getDaoClass() {
        return (Class<PermissionCollectionDao<PermissionCollection>>) new PermissionCollectionDao<PermissionCollection>().getClass();
    }

}
