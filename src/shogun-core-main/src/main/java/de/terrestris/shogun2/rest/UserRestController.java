package de.terrestris.shogun2.rest;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.dao.UserDao;
import de.terrestris.shogun2.model.User;
import de.terrestris.shogun2.model.UserGroup;
import de.terrestris.shogun2.service.UserService;

/**
 * @author Kai Volland
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/users")
public class UserRestController<E extends User, D extends UserDao<E>, S extends UserService<E, D>>
    extends AbstractRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public UserRestController() {
        this((Class<E>) User.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected UserRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("userService")
    public void setService(S service) {
        this.service = service;
    }

    /**
     * Get the groups of a specific user.
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/{userId}/userGroups", method = RequestMethod.GET)
    public ResponseEntity<Set<UserGroup>> findGroupsOfUser(@PathVariable Integer userId) {

        try {
            Set<UserGroup> userGroupsSet = this.service.getGroupsOfUser(userId);
            return new ResponseEntity<Set<UserGroup>>(userGroupsSet, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error finding user with id " + userId + ": "
                + e.getMessage());
            return new ResponseEntity<Set<UserGroup>>(HttpStatus.NOT_FOUND);
        }
    }
}
