package de.terrestris.shogun2.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.terrestris.shogun2.model.Application;

/**
 * @author Kai Volland
 *
 */
@RestController
@RequestMapping("/application")
public class ApplicationRestController extends
		AbstractRestController<Application> {

}
