package de.terrestris.shogun2.service;

import org.springframework.stereotype.Service;

import de.terrestris.shogun2.model.Application;

/**
 * Service class for the {@link Application} model.
 * 
 * @author Nils Bühner
 * @see AbstractCrudService
 * 
 */
@Service("applicationService")
public class ApplicationService extends AbstractCrudService<Application> {

}
