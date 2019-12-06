package de.terrestris.shoguncore.rest;

import de.terrestris.shoguncore.dao.UserGroupDao;
import de.terrestris.shoguncore.model.User;
import de.terrestris.shoguncore.model.UserGroup;
import de.terrestris.shoguncore.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author Johannes Weskamm
 * @author Nils Bühner
 */
@RestController
@RequestMapping("/groups")
public class UserGroupRestController<E extends UserGroup, D extends UserGroupDao<E>, S extends UserGroupService<E, D>>
    extends AbstractRestController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public UserGroupRestController() {
        this((Class<E>) UserGroup.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected UserGroupRestController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("userGroupService")
    public void setService(S service) {
        this.service = service;
    }

    /**
     * Get the users of a specific group.
     *
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/{groupId}/users", method = RequestMethod.GET)
    public ResponseEntity<Set<User>> findUsersOfGroup(@PathVariable Integer groupId) {

        try {
            Set<User> groupUsersSet = this.service.getUsersOfGroup(groupId);
            return new ResponseEntity<Set<User>>(groupUsersSet, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error finding group with id " + groupId + ": "
                + e.getMessage());
            return new ResponseEntity<Set<User>>(HttpStatus.NOT_FOUND);
        }
    }
}
