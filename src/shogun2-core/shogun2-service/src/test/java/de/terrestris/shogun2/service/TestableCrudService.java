package de.terrestris.shogun2.service;

import org.mockito.InjectMocks;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * This (empty) class only exists to test the {@link AbstractCrudService}, which
 * cannot be instantiated by Mockito when using {@link InjectMocks}.
 * 
 * @author Nils Bühner
 * 
 */
public class TestableCrudService extends AbstractCrudService<PersistentObject> {
}
