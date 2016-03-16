package de.terrestris.shogun2.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hibernate.criterion.Order;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResult;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;
import de.terrestris.shogun2.dao.GenericHibernateDao;
import de.terrestris.shogun2.helper.IdHelper;
import de.terrestris.shogun2.model.PersistentObject;
import de.terrestris.shogun2.paging.PagingResult;

/**
 * Test for the {@link AbstractExtDirectCrudService}.
 *
 * @author Nils Bühner
 *
 */
public abstract class AbstractExtDirectCrudServiceTest<E extends PersistentObject, D extends GenericHibernateDao<E, Integer>, S extends AbstractExtDirectCrudService<E, D>> {

	/**
	 * Object that holds concrete implementations of {@link PersistentObject}
	 * for the tests.
	 */
	protected PersistentObject implToTest = null;

	protected D dao;

	@InjectMocks
	protected S crudService;

	@Before
	public void setUp() {
		// see here why we are mocking this way:
		// http://stackoverflow.com/a/24302622
		dao = mock(getDaoClass());
		this.crudService = getCrudService();
		// Process mock annotations
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * This method has to be implemented by subclasses.
	 *
	 * @throws Exception
	 */
	@Before
	public abstract void setUpImplToTest() throws Exception;

	/**
	 * This method has to be implemented by subclasses to return a concrete
	 * implementation of the tested service.
	 *
	 * @return
	 */
	protected abstract S getCrudService();

	/**
	 * This method has to be implemented by subclasses to return the concrete
	 * class of the dao.
	 *
	 * @return
	 */
	protected abstract Class<D> getDaoClass();


	@After
	public void tearDownAfterEachTest() throws Exception {
		implToTest = null;
	}

	/**
	 * Test whether the saveOrUpdate method <i>saves</i> a
	 * {@link PersistentObject}, which initially has no id value.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void saveOrUpdate_shouldSave() {

		doAnswer(new Answer<E>() {
			public E answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException {
				E po = (E) invocation.getArguments()[0];

				// set id like the dao does
				IdHelper.setIdOnPersistentObject(po, 1);

				return po;
			}
		}).when(dao).saveOrUpdate((E) implToTest);

		// id has to be NULL before the service method is called
		assertNull(implToTest.getId());

		implToTest = crudService.saveOrUpdate((E) implToTest);

		// id must not be NULL after the service method is called
		assertNotNull(implToTest.getId());
		assertTrue(implToTest.getId() > 0);

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).saveOrUpdate((E) implToTest);
	}

	/**
	 * Test whether
	 * {@link ExtDirectAbstractCrudService#saveOrUpdate(PersistentObject)}
	 * <i>updates</i> the modified value of a {@link PersistentObject}.
	 *
	 *
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void saveOrUpdate_shouldUpdate() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException,
			IllegalAccessException, InterruptedException {

		Integer id = 42;
		ReadableDateTime created = implToTest.getCreated();
		ReadableDateTime modified = implToTest.getModified();

		IdHelper.setIdOnPersistentObject(implToTest, id);

		doAnswer(new Answer<E>() {
			public E answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException,
					InterruptedException {
				E po = (E) invocation.getArguments()[0];

				// wait and can change the modified value
				Thread.sleep(1);
				po.setModified(DateTime.now());

				return po;
			}
		}).when(dao).saveOrUpdate((E) implToTest);

		// now call the method to test
		implToTest = crudService.saveOrUpdate((E) implToTest);

		// id and created should not have changed
		assertEquals(id, implToTest.getId());
		assertEquals(created, implToTest.getCreated());

		// modified should have changed
		assertTrue(implToTest.getModified().isAfter(modified));

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).saveOrUpdate((E) implToTest);
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

		IdHelper.setIdOnPersistentObject(implToTest, existingId);

		// mock dao behavior
		doReturn(implToTest).when(dao).findById(existingId);

		// actually test
		E result = crudService.findById(existingId);

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
		E result = crudService.findById(nonExistingId);

		assertNull(result);

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).findById(nonExistingId);
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 *
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void findAll_shouldFindAll() throws NoSuchFieldException,
			IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {

		final Integer id1 = 17;
		final Integer id2 = 42;
		List<E> persistedObjectList = new ArrayList<E>();

		E obj1 = (E) implToTest;
		E obj2 = (E) BeanUtils.cloneBean(obj1);

		IdHelper.setIdOnPersistentObject(obj1, id1);
		IdHelper.setIdOnPersistentObject(obj2, id2);

		persistedObjectList.add((E) obj1);
		persistedObjectList.add((E) obj2);

		// mock dao behaviour
		doReturn(persistedObjectList).when(dao).findAll();

		// actually test
		List<E> resultList = crudService.findAll();

		assertNotNull(resultList);
		assertEquals(2, resultList.size());

		Matcher<E> m1 = hasProperty("id", is(id1));
		Matcher<E> m2 = hasProperty("id", is(id2));

		assertThat(resultList,
				IsIterableContainingInAnyOrder.<E> containsInAnyOrder(m1, m2));

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).findAll();
	}

	/**
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 *
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void delete_shouldDelete() throws NoSuchFieldException,
			IllegalAccessException {

		Set<E> persistedObjects = new HashSet<E>();
		persistedObjects.add((E) implToTest);

		doNothing().when(dao).delete((E) implToTest);

		// actually test
		crudService.delete((E) implToTest);

		// be sure that dao.delete() has been executed exactly once
		verify(dao, times(1)).delete((E) implToTest);

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

		IdHelper.setIdOnPersistentObject(implToTest, existingId);

		// mock dao behavior
		doReturn(implToTest).when(dao).findById(existingId);

		// actually test
		E result = crudService.formLoadById(existingId);

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
		E result = crudService.formLoadById(nonExistingId);

		assertNull(result);

		// be sure that dao method has been executed exactly once
		verify(dao, times(1)).findById(nonExistingId);
	}

	/**
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 *
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void saveOrUpdateCollection_shouldSaveCollection()
			throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {

		E obj1 = (E) implToTest;
		E obj2 = (E) BeanUtils.cloneBean(obj1);

		doAnswer(new Answer<E>() {
			int nextId = 1;

			public E answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException {
				E po = (E) invocation.getArguments()[0];

				// set id like the dao does
				IdHelper.setIdOnPersistentObject(po, nextId);
				nextId++;

				return po;
			}
		}).when(dao).saveOrUpdate((E) any(PersistentObject.class));

		// id has to be NULL before the service method is called
		assertNull(obj1.getId());
		assertNull(obj2.getId());

		List<E> listToSave = new ArrayList<E>();
		listToSave.add(obj1);
		listToSave.add(obj2);

		// actually test
		Collection<E> resultCollection = crudService
				.saveOrUpdateCollection(listToSave);

		// id must not be NULL after the service method is called
		assertNotNull(resultCollection);
		assertEquals(2, resultCollection.size());

		assertTrue(obj1.getId() > 0);
		assertTrue(obj2.getId() > 0);

		// be sure that dao method has been executed for each object
		verify(dao, times(2)).saveOrUpdate((E) any(PersistentObject.class));
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void findWithSortingAndPagingExtDirect_shouldDoSortingAndPaging() {

		final Integer totalAvailableRecords = 100;

		// mock dao behavior
		doAnswer(new Answer<PagingResult<E>>() {
			public PagingResult<E> answer(InvocationOnMock invocation)
					throws NoSuchFieldException, SecurityException,
					IllegalArgumentException, IllegalAccessException,
					InstantiationException, InvocationTargetException,
					NoSuchMethodException {
				Integer firstResult = (Integer) invocation.getArguments()[0];
				Integer maxResults = (Integer) invocation.getArguments()[1];
				List<Order> hibernateSorters = (List<Order>) invocation
						.getArguments()[2];

				List<E> records = new ArrayList<E>();

				// build some objects

				// assume to have only one sorter
				Order hibernateSorter = hibernateSorters.get(0);
				if (hibernateSorter.isAscending()) {
					for (int i = firstResult; i < firstResult + maxResults; i++) {
						E obj = (E) createPersistentObject(i);
						records.add(obj);
					}
				} else {
					for (int i = firstResult + maxResults; i > firstResult; i--) {
						E obj = (E) createPersistentObject(i);
						records.add(obj);
					}
				}

				PagingResult<E> pagingResult = new PagingResult<E>(records,
						totalAvailableRecords);
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
		ExtDirectStoreResult<E> extResult = crudService
				.findWithSortingAndPagingExtDirect(request);

		assertNotNull(extResult);

		List<E> extResultRecords = (List<E>) extResult.getRecords();
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

		extResult = crudService.findWithSortingAndPagingExtDirect(request);

		assertNotNull(extResult);

		extResultRecords = (List<E>) extResult.getRecords();
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
	 * @param id
	 *            the ID to set on the {@link PersistentObject}
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	private E createPersistentObject(int id) throws NoSuchFieldException,
			IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException {
		@SuppressWarnings("unchecked")
		E obj = (E) BeanUtils.cloneBean(implToTest);
		IdHelper.setIdOnPersistentObject(obj, id);
		return obj;
	}

}
