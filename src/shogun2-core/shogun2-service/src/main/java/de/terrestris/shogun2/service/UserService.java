package de.terrestris.shogun2.service;

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

}
