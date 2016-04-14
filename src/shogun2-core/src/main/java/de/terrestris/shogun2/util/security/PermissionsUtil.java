package de.terrestris.shogun2.util.security;

import java.util.Arrays;
import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import de.terrestris.shogun2.dao.PermissionCollectionDao;
import de.terrestris.shogun2.model.security.Permission;
import de.terrestris.shogun2.model.security.PermissionCollection;
import de.terrestris.shogun2.service.PermissionCollectionService;

/**
*
* @author Johannes Weskamm
* @author terrestris GmbH & Co. KG
*
*/
@Component
public class PermissionsUtil {

	private static PermissionCollectionService<PermissionCollection,PermissionCollectionDao<PermissionCollection>> permissionCollectionService;

	@Autowired
	@Qualifier("permissionCollectionService")
	private PermissionCollectionService<PermissionCollection, PermissionCollectionDao<PermissionCollection>> tPermissionCollectionService;

	/**
	 * Using postConstruct due to static methods, see
	 * http://stackoverflow.com/a/17660550 for details
	 */
	@PostConstruct
	public void init() {
		PermissionsUtil.permissionCollectionService = tPermissionCollectionService;
	}

	/**
	 *
	 * @return
	 */
	public static PermissionCollection getPermissionCollection(Permission... permissions) {
		PermissionCollection simplePermissionCollection =
				new PermissionCollection();
		simplePermissionCollection.getPermissions().addAll(
				new HashSet<Permission>(Arrays.asList(permissions)));
		permissionCollectionService.saveOrUpdate(simplePermissionCollection);
		return simplePermissionCollection;
	}

	/**
	 * @return the tPermissionCollectionService
	 */
	public PermissionCollectionService<PermissionCollection, PermissionCollectionDao<PermissionCollection>> gettPermissionCollectionService() {
		return tPermissionCollectionService;
	}

	/**
	 * @param tPermissionCollectionService the tPermissionCollectionService to set
	 */
	public void settPermissionCollectionService(
			PermissionCollectionService<PermissionCollection, PermissionCollectionDao<PermissionCollection>> tPermissionCollectionService) {
		this.tPermissionCollectionService = tPermissionCollectionService;
	}
}

