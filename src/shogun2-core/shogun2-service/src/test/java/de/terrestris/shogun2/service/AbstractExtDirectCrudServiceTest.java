package de.terrestris.shogun2.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hibernate.criterion.Order;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;
import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.paging.PagingResult;

/**
 * Test for the {@link AbstractCrudService}.
 * 
 * @author Nils Bühner
 * 
 */
public class AbstractExtDirectCrudServiceTest {

	@Mock
	private GenericHibernateDao<PersistentObject, Integer> dao;

	@InjectMocks
	private TestableExtDirectCrudService extDirectCrudService;

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

		persistentObject = extDirectCrudService.saveOrUpdate(persistentObject);

		// id must not be NULL after the service method is called
		assertNotNull(persistentObject.getId());
		assertTrue(persistentObject.getId() > 0);

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).saveOrUpdate(persistentObject);
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
		persistentObject = extDirectCrudService.saveOrUpdate(persistentObject);

		// id and created should not have changed
		assertEquals(id, persistentObject.getId());
		assertEquals(created, persistentObject.getCreated());

		// modified should have changed
		assertTrue(persistentObject.getModified().isAfter(modified));

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).saveOrUpdate(persistentObject);
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * 
	 */
	@Test
	public void findById_shouldFindExistingId() throws NoSuchFieldException,
			IllegalAccessException {

		Integer existingId = 17;

		@SuppressWarnings("serial")
		PersistentObject objectToFind = new PersistentObject() {
		};

		setIdOnPersistentObject(objectToFind, existingId);

		// mock dao behavior
		doReturn(objectToFind).when(dao).findById(existingId);

		// actually test
		PersistentObject result = extDirectCrudService.findById(existingId);

		assertNotNull(result);
		assertEquals(existingId, result.getId());

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).findById(existingId);
	}

	/**
	 *
	 */
	@Test
	public void saveOrUpdate_shouldReturnNullForNonExistingId() {

		Integer nonExistingId = 42;

		// mock behaviour of dao, which is called in the service
		doReturn(null).when(dao).findById(nonExistingId);

		// actually test
		PersistentObject result = extDirectCrudService.findById(nonExistingId);

		assertNull(result);

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).findById(nonExistingId);
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * 
	 */
	@SuppressWarnings({ "unchecked", "serial" })
	@Test
	public void findAll_shouldFindAll() throws NoSuchFieldException,
			IllegalAccessException {

		final Integer id1 = 17;
		final Integer id2 = 42;
		List<PersistentObject> persistedObjectList = new ArrayList<PersistentObject>();

		PersistentObject obj1 = new PersistentObject() {
		};
		PersistentObject obj2 = new PersistentObject() {
		};

		setIdOnPersistentObject(obj1, id1);
		setIdOnPersistentObject(obj2, id2);

		persistedObjectList.add(obj1);
		persistedObjectList.add(obj2);

		// mock dao behaviour
		doReturn(persistedObjectList).when(dao).findAll();

		// actually test
		List<PersistentObject> resultList = extDirectCrudService.findAll();

		assertNotNull(resultList);
		assertEquals(2, resultList.size());

		Matcher<PersistentObject> m1 = hasProperty("id", is(id1));
		Matcher<PersistentObject> m2 = hasProperty("id", is(id2));

		assertThat(resultList,
				IsIterableContainingInAnyOrder
						.<PersistentObject> containsInAnyOrder(m1, m2));

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).findAll();
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * 
	 */
	@Test
	public void delete_shouldDelete() throws NoSuchFieldException,
			IllegalAccessException {

		@SuppressWarnings("serial")
		PersistentObject objectToDelete = new PersistentObject() {
		};

		Set<PersistentObject> persistedObjects = new HashSet<PersistentObject>();
		persistedObjects.add(objectToDelete);

		doNothing().when(dao).delete(objectToDelete);

		// actually test
		extDirectCrudService.delete(objectToDelete);

		// be sure that dao.delete() has been executed exactly once
		verify(dao, times(1)).delete(objectToDelete);

		// maybe this test can be enhanced...
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * 
	 */
	@Test
	public void formLoadById_shouldLoadExistingId()
			throws NoSuchFieldException, IllegalAccessException {

		Integer existingId = 17;

		@SuppressWarnings("serial")
		PersistentObject objectToLoad = new PersistentObject() {
		};

		setIdOnPersistentObject(objectToLoad, existingId);

		// mock dao behavior
		doReturn(objectToLoad).when(dao).findById(existingId);

		// actually test
		PersistentObject result = extDirectCrudService.formLoadById(existingId);

		assertNotNull(result);
		assertEquals(existingId, result.getId());

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).findById(existingId);
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * 
	 */
	@Test
	public void formLoadById_shouldReturnNullForNonExistingId()
			throws NoSuchFieldException, IllegalAccessException {

		Integer nonExistingId = 42;

		// mock dao behavior
		doReturn(null).when(dao).findById(nonExistingId);

		// actually test
		PersistentObject result = extDirectCrudService
				.formLoadById(nonExistingId);

		assertNull(result);

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).findById(nonExistingId);
	}

	/**
	 *
	 */
	@SuppressWarnings("serial")
	@Test
	public void saveOrUpdateCollection_shouldSaveCollection() {

		PersistentObject obj1 = new PersistentObject() {
		};
		PersistentObject obj2 = new PersistentObject() {
		};

		doAnswer(new Answer<PersistentObject>() {
			int nextId = 1;

			public PersistentObject answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException {
				PersistentObject po = (PersistentObject) invocation
						.getArguments()[0];

				// set id like the dao does
				setIdOnPersistentObject(po, nextId);
				nextId++;

				return po;
			}
		}).when(dao).saveOrUpdate(any(PersistentObject.class));

		// id has to be NULL before the service method is called
		assertNull(obj1.getId());
		assertNull(obj2.getId());

		List<PersistentObject> listToSave = new ArrayList<PersistentObject>();
		listToSave.add(obj1);
		listToSave.add(obj2);

		// actually test
		Collection<PersistentObject> resultCollection = extDirectCrudService
				.saveOrUpdateCollection(listToSave);

		// id must not be NULL after the service method is called
		assertNotNull(resultCollection);
		assertEquals(2, resultCollection.size());

		assertTrue(obj1.getId() > 0);
		assertTrue(obj2.getId() > 0);

		// be sure that dao method has been executed for each object
		verify(dao, times(2)).saveOrUpdate(obj1);
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void findWithSortingAndPagingExtDirect_shouldDoSortingAndPaging() {

		final Integer totalAvailableRecords = 100;

		// mock dao behavior
		doAnswer(new Answer<PagingResult<PersistentObject>>() {
			public PagingResult<PersistentObject> answer(
					InvocationOnMock invocation) throws NoSuchFieldException,
					SecurityException, IllegalArgumentException,
					IllegalAccessException {
				Integer firstResult = (Integer) invocation.getArguments()[0];
				Integer maxResults = (Integer) invocation.getArguments()[1];
				List<Order> hibernateSorters = (List<Order>) invocation
						.getArguments()[2];

				List<PersistentObject> records = new ArrayList<PersistentObject>();

				// build some objects

				// assume to have only one sorter
				Order hibernateSorter = hibernateSorters.get(0);
				if (hibernateSorter.isAscending()) {
					for (int i = firstResult; i < firstResult + maxResults; i++) {
						PersistentObject obj = createPersistentObject(i);
						records.add(obj);
					}
				} else {
					for (int i = firstResult + maxResults; i > firstResult; i--) {
						PersistentObject obj = createPersistentObject(i);
						records.add(obj);
					}
				}

				PagingResult<PersistentObject> pagingResult = new PagingResult<PersistentObject>(
						records, totalAvailableRecords);
				return pagingResult;
			}

		}).when(dao).findByCriteriaWithSortingAndPaging(anyInt(), anyInt(),
				anyListOf(Order.class));

		// mock a first request with ASCENDING order
		ExtDirectStoreReadRequest request = new ExtDirectStoreReadRequest();

		SortInfo ascSorter = new SortInfo("id", SortDirection.ASCENDING);
		List<SortInfo> sorters = new ArrayList<SortInfo>();
		sorters.add(ascSorter);

		Integer firstResult = 10;
		Integer maxResults = 5;

		request.setStart(firstResult);
		request.setLimit(maxResults);
		request.setSorters(sorters);

		// actually make a first test
		ExtDirectStoreResult<PersistentObject> extResult = extDirectCrudService
				.findWithSortingAndPagingExtDirect(request);

		assertNotNull(extResult);

		List<PersistentObject> extResultRecords = (List<PersistentObject>) extResult
				.getRecords();
		assertNotNull(extResultRecords);
		assertEquals(maxResults.intValue(), extResultRecords.size());

		assertEquals(totalAvailableRecords.longValue(), extResult.getTotal()
				.longValue());

		Integer idOfFirstRec = extResultRecords.get(0).getId();
		Integer idOfLastRec = extResultRecords.get(extResultRecords.size() - 1)
				.getId();

		assertTrue(idOfFirstRec < idOfLastRec);
		assertTrue(idOfFirstRec == idOfLastRec - maxResults + 1);

		// make a second test for DESCENDING order
		SortInfo descSorter = new SortInfo("id", SortDirection.DESCENDING);
		sorters.clear();
		sorters.add(descSorter);

		extResult = extDirectCrudService
				.findWithSortingAndPagingExtDirect(request);

		assertNotNull(extResult);

		extResultRecords = (List<PersistentObject>) extResult.getRecords();
		assertNotNull(extResultRecords);
		assertEquals(maxResults.intValue(), extResultRecords.size());

		assertEquals(totalAvailableRecords.longValue(), extResult.getTotal()
				.longValue());

		idOfFirstRec = extResultRecords.get(0).getId();
		idOfLastRec = extResultRecords.get(extResultRecords.size() - 1).getId();

		assertTrue(idOfFirstRec > idOfLastRec);
		assertTrue(idOfLastRec == idOfFirstRec - maxResults + 1);

		// be sure that dao method has been executed exactly once (ASC and DESC
		// test)
		verify(dao, times(2)).findByCriteriaWithSortingAndPaging(anyInt(),
				anyInt(), anyListOf(Order.class));
	}

	/**
	 * @param i
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("serial")
	private PersistentObject createPersistentObject(int i)
			throws NoSuchFieldException, IllegalAccessException {
		PersistentObject obj = new PersistentObject() {
		};
		setIdOnPersistentObject(obj, i);
		System.out.println("+++ set " + i);
		return obj;
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
