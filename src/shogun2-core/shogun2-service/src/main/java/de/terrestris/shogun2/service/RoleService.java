package de.terrestris.shogun2.service;

import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.model.Role;

/**
 * Service class for the {@link Role} model.
 *
 * @author Nils Bühner
 * @see AbstractExtDirectCrudService
 *
 */
@Service("roleService")
public class RoleService extends AbstractExtDirectCrudService<Role> {

	/**
	 * Returns the role for the given (unique) role name.
	 * If no role was found, null will be returned.
	 *
	 * @param roleName A unique role name.
	 * @return The unique role for the role name or null.
	 */
	public Role findByRoleName(String roleName) {

		SimpleExpression eqRoleName =
			Restrictions.eq("name", roleName);

		Role role = dao.findByUniqueCriteria(eqRoleName);

		return role;
	}

}
