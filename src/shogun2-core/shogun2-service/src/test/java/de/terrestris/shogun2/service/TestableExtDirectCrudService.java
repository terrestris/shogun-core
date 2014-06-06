package de.terrestris.shogun2.service;

import org.mockito.InjectMocks;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * This (empty) class only exists to test the
 * {@link AbstractExtDirectCrudService}, which cannot be instantiated by Mockito
 * when using {@link InjectMocks}. By testing
 * {@link AbstractExtDirectCrudService}, we inherently test the
 * {@link AbstractCrudService}.
 * 
 * @author Nils Bühner
 * 
 */
public class TestableExtDirectCrudService extends
		AbstractExtDirectCrudService<PersistentObject> {
}
