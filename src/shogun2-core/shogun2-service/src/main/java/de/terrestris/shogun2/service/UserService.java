package de.terrestris.shogun2.service;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Service;

import de.terrestris.shogun2.model.User;

/**
 * Service class for the {@link User} model.
 * 
 * @author Nils Bühner
 * @see AbstractCrudService
 * 
 */
@Service("userService")
public class UserService extends AbstractExtDirectCrudService<User> {

	/**
	 * 
	 * @param accountName
	 * @return
	 */
	public User findByAccountName(String accountName) {

		SimpleExpression eqAccountName = Restrictions.eq("accountName",
				accountName);
		List<User> userList = dao.findByCriteria(eqAccountName);

		if (userList.size() == 1) {
			return userList.get(0);
		}

		return null;
	}

}
