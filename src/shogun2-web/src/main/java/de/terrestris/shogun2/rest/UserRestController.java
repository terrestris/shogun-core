package de.terrestris.shogun2.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.model.User;

/**
 * @author Kai Volland
 * @author Nils Bühner
 *
 */
@RestController
@RequestMapping("/users")
public class UserRestController extends
		AbstractRestController<User> {

}
