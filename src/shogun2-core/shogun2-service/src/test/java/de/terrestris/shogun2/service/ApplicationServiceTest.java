package de.terrestris.shogun2.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.terrestris.shogun2.dao.ApplicationDao;
import de.terrestris.shogun2.model.Application;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.service.ApplicationService;

public class ApplicationServiceTest {

	@Mock
	private ApplicationDao applicationDao;

	@InjectMocks
	private ApplicationService applicationService;

	@Before
	public void setUp() {
		// Process mock annotations
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void createApplication_shouldCreateApplication() {

		Application application = new Application();

		doAnswer(new Answer<Application>() {
			public Application answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException {
				Application application = (Application) invocation
						.getArguments()[0];

				// modify the final field 'id' to simulate the behavior of the
				// saveOrUpdate-method of hibernate, which is called in the
				// service

				Field idField = PersistentObject.class.getDeclaredField("id");

				idField.setAccessible(true);
				idField.set(application, 1);
				idField.setAccessible(false);

				return application;
			}
		}).when(applicationDao).saveOrUpdate(application);

		// id has to be NULL before the service method is called
		assertNull(application.getId());

		application = applicationService.createApplication(application);

		// id must not be NULL after the service method is called
		assertNotNull(application.getId());
		assertTrue(application.getId() > 0);
	}
}
