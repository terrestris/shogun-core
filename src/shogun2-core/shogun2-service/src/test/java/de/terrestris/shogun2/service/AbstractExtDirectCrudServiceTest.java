package de.terrestris.shogun2.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.PersistentObject;

/**
 * Test for the {@link AbstractCrudService}.
 * 
 * @author Nils Bühner
 * 
 */
public class AbstractCrudServiceTest {

	@Mock
	private GenericHibernateDao<PersistentObject, Integer> dao;

	@InjectMocks
	private TestableCrudService crudService;

	@Before
	public void setUp() {
		// Process mock annotations
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test whether the saveOrUpdate method <i>saves</i> a
	 * {@link PersistentObject}, which initially has no id value.
	 */
	@Test
	public void saveOrUpdate_shouldSave() {

		@SuppressWarnings("serial")
		PersistentObject persistentObject = new PersistentObject() {
		};

		doAnswer(new Answer<PersistentObject>() {
			public PersistentObject answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException {
				PersistentObject po = (PersistentObject) invocation
						.getArguments()[0];

				// set id like the dao does
				setIdOnPersistentObject(po, 1);

				return po;
			}
		}).when(dao).saveOrUpdate(persistentObject);

		// id has to be NULL before the service method is called
		assertNull(persistentObject.getId());

		persistentObject = crudService.saveOrUpdate(persistentObject);

		// id must not be NULL after the service method is called
		assertNotNull(persistentObject.getId());
		assertTrue(persistentObject.getId() > 0);
	}

	/**
	 * Test whether {@link AbstractCrudService#saveOrUpdate(PersistentObject)}
	 * <i>updates</i> the modified value of a {@link PersistentObject}.
	 * 
	 * 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InterruptedException
	 */
	@Test
	public void saveOrUpdate_shouldUpdate() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException,
			IllegalAccessException, InterruptedException {

		@SuppressWarnings("serial")
		PersistentObject persistentObject = new PersistentObject() {
		};
		Integer id = 42;
		ReadableDateTime created = persistentObject.getCreated();
		ReadableDateTime modified = persistentObject.getModified();

		setIdOnPersistentObject(persistentObject, id);

		doAnswer(new Answer<PersistentObject>() {
			public PersistentObject answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException,
					InterruptedException {
				PersistentObject po = (PersistentObject) invocation
						.getArguments()[0];

				// wait and can change the modified value
				Thread.sleep(1);
				po.setModified(DateTime.now());

				return po;
			}
		}).when(dao).saveOrUpdate(persistentObject);

		// now call the method to test
		persistentObject = crudService.saveOrUpdate(persistentObject);

		// id and created should not have changed
		assertEquals(id, persistentObject.getId());
		assertEquals(created, persistentObject.getCreated());

		// modified should have changed
		assertTrue(persistentObject.getModified().isAfter(modified));
	}

	/**
	 * Helper method that uses reflection to set the (inaccessible) id field of
	 * the given {@link PersistentObject}.
	 * 
	 * @param persistentObject
	 *            The object with the inaccessible id field
	 * @param id
	 *            The id to set
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	private static final void setIdOnPersistentObject(
			PersistentObject persistentObject, Integer id)
			throws NoSuchFieldException, IllegalAccessException {
		// use reflection to get the inaccessible final field 'id'
		Field idField = PersistentObject.class.getDeclaredField("id");

		// make the field accessible and set the value
		idField.setAccessible(true);
		idField.set(persistentObject, id);
		idField.setAccessible(false);
	}
}
