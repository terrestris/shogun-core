package de.terrestris.shogun2.service;

import org.springframework.stereotype.Service;

import de.terrestris.shogun2.model.module.Module;

/**
 * Service class for the {@link Module} model.
 * 
 * @author Nils Bühner
 * @see AbstractCrudService
 * 
 */
@Service("moduleService")
public class ModuleService extends AbstractCrudService<Module> {

}
