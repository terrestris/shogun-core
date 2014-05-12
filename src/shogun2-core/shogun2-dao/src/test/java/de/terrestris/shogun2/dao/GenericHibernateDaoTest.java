package de.terrestris.shogun2.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.model.Application;

/**
 * The most basic concrete class extending the {@link GenericHibernateDao}, will
 * be used to test the functionality that the abstract class provides. This
 * class will operate on the {@link Application} class, but any model-class
 * could have been picked.
 */
@Repository
class ConcreteDao extends GenericHibernateDao<Application, Integer> {
	protected ConcreteDao() {
		super(Application.class);
	}
}

/**
 * This class will test the {@link GenericHibernateDao}. As {@link
 * GenericHibernateDao} is an abstract class, we cannot instantiate it. Instead
 * we will use the {@link ConcreteDao} (as the most basic extension of {@link
 * GenericHibernateDao}) to test the logic contained in {@link
 * GenericHibernateDao}.
 *
 * @author Marc Jansen
 * @author Nils Bühner
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-context-dao.xml" })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class GenericHibernateDaoTest {


	/**
	 * We use the {@link ConcreteDao} to test the behaviour of the
	 * {@link GenericHibernateDao} (which is an abstract class and cannot be
	 * instantiated).
	 */
	@Autowired
	ConcreteDao dao;

	private Set<String> usedRandomStrings = new HashSet<String>();

	/**
	 * @return
	 */
	private String getRandomStr() {
		String candidate;
		while (true) {
			candidate = RandomStringUtils.random(10);
			if (usedRandomStrings.contains(candidate) == false) {
				// stop it, we found one
				usedRandomStrings.add(candidate);
				break;
			}
		}
		return candidate;
	}

	/**
	 * Small helper for getting a new unsaved mock Application with a random
	 * name.
	 *
	 * @return
	 */
	private Application getRandomUnsavedMockApp() {
		String randomName = "A name " + getRandomStr();
		return getMockApp(randomName);
	}

	/**
	 * Small helper for getting a new saved mock Application with a random name.
	 *
	 * @return
	 */
	private Application getRandomSavedMockApp() {
		String randomName = "A name " + getRandomStr();
		return getSavedMockApp(randomName);
	}

	/**
	 * Small helper for getting a new unsaved mock Application.
	 *
	 * @return
	 */
	private Application getMockApp(String name) {
		Application app = new Application();
		app.setName(name);
		app.setDescription("A description");
		return app;
	}

	/**
	 * Small helper for getting a new saved mock Application.
	 *
	 * @return
	 */
	private Application getSavedMockApp(String name) {
		String appName = name;
		if (name.isEmpty()) {
			appName = "A name";
		}

		Application app = getMockApp(appName);

		dao.saveOrUpdate(app);

		return app;
	}

	/**
	 * Tests whether the automatic setting of field modified happens when
	 * creating.
	 */
	@Test
	public void saveOrUpdate_shouldSetModified() {
		// first create an application and save it.
		Application app = this.getRandomUnsavedMockApp();

		// Model tests should ensure that the constructor populates modified
		ReadableDateTime before = app.getModified();

		try {
			// ... wait ...
			Thread.sleep(1);

			// ... then save
			dao.saveOrUpdate(app);

			ReadableDateTime after = app.getModified();

			// They should be different
			assertNotEquals(before, after);

			// after should be greater than before
			boolean isLater = before.compareTo(after) == -1;
			assertTrue(isLater);
		} catch (InterruptedException e) {
			fail("Caught exception while attempting to wait");
			return;
		}
	}

	/**
	 * Tests whether saveOrUpdate cannot be tricked by explicitly setting field
	 * modified.
	 */
	@Test
	public void saveOrUpdate_shouldAlwaysSetModified() {
		// first create an application and save it.
		Application app = this.getRandomUnsavedMockApp();

		try {
			// ... wait ...
			Thread.sleep(1);

			// ... then update the modified field
			DateTime before = DateTime.now();
			app.setModified(before);

			// ... wait ...
			Thread.sleep(1);

			// ... and only then save, modified should differ now
			dao.saveOrUpdate(app);
			ReadableDateTime after = app.getModified();

			// They should be different
			assertNotEquals(before, after);

			// after should be greater than before
			boolean isLater = before.compareTo(after) == -1;
			assertTrue(isLater);

		} catch (InterruptedException e) {
			fail("Caught exception while attempting to wait");
			return;
		}
	}

	/**
	 * Tests whether an UPDATE results in updating the field modified.
	 */
	@Test
	public void saveOrUpdate_shouldUpdateModified() {
		// first create an application and save it.
		Application app = new Application("Some Name", "Some description");
		dao.saveOrUpdate(app);

		ReadableDateTime before = app.getModified();

		try {
			Thread.sleep(1);
			// change the application
			app.setName("Some other name");
			app.setDescription("Changed description");
			dao.saveOrUpdate(app);
			ReadableDateTime after = app.getModified();

			// They should be different
			assertNotEquals(before, after);

			// after should be greater than before
			boolean isLater = before.compareTo(after) == -1;
			assertTrue(isLater);
		} catch (InterruptedException e) {
			fail("Caught exception while attempting to wait");
			return;
		}
	}

	/**
	 * Tests whether we can both CREATE and UPDATE with saveOrUpdate
	 */
	@Test
	public void saveOrUpdate_shouldSaveOrUpdateApplication() {
		Application app = getRandomUnsavedMockApp();

		assertNull(app.getId());

		// Test CREATE
		dao.saveOrUpdate(app);

		Integer id = app.getId();

		assertNotNull(id);
		assertTrue(id > 0);

		// Test UPDATE
		String changedNameOfApp = "Some other name";
		String changedDescOfApp = "Changed description";

		app.setName(changedNameOfApp);
		app.setDescription(changedDescOfApp);

		dao.saveOrUpdate(app);

		assertEquals(id, app.getId());
		assertEquals(changedNameOfApp, app.getName());
		assertEquals(changedDescOfApp, app.getDescription());
	}

	/**
	 * Tests whether we can retrieve saved applications by id.
	 */
	@Test
	public void findById_shouldReturnNullForNonExistingId() {
		Application app = dao.findById(-90210);
		assertNull(app);
	}

	/**
	 * Tests whether we can retrieve saved applications by id.
	 */
	@Test
	public void findById_shouldRetrieveApplication() {
		Application app = getRandomSavedMockApp();
		Integer id = app.getId();
		Application queriedApp = dao.findById(id);

		// ... verify we could get it:
		assertNotNull(queriedApp);
		assertEquals(app, queriedApp);
	}

	/**
	 * Tests whether we can correctly retrieve saved applications by id.
	 */
	@Test
	public void findById_shouldRetrieveCorrectResults() {
		Application firstApp = getRandomSavedMockApp();
		Application secondApp = getRandomSavedMockApp();

		assertNotEquals(firstApp, secondApp);

		Integer firstId = firstApp.getId();
		Integer secondId = secondApp.getId();

		Application queriedFirst = dao.findById(firstId);
		Application queriedSecond = dao.findById(secondId);

		assertEquals(firstApp, queriedFirst);
		assertEquals(secondApp, queriedSecond);
	}

	/**
	 * Tests whether we can delete applications.
	 */
	@Test
	public void delete_shouldDelete() {
		Application app = getRandomSavedMockApp();
		Integer id = app.getId();
		int numBefore = dao.findAll().size();

		// ... delete the app
		dao.delete(app);

		int numAfter = dao.findAll().size();

		// ...try to get it by id
		Application queriedAppAfter = dao.findById(id);

		assertNull(queriedAppAfter);
		assertEquals(numBefore, numAfter + 1);
	}

	/**
	 * Tests whether deletion with unsaved apps doesn't throw an exception.
	 */
	@Test
	public void delete_shouldNotThrowWhenUnsavedAppIsPassed() {
		Application app = getRandomUnsavedMockApp();

		// ... delete the unsaved app
		dao.delete(app);

		assertTrue(true);
	}

	/**
	 * Tests whether findAll() gives us an empty list when nothing is persisted.
	 */
	@Test
	public void findAll_shouldReturnEmptyListWhenNothingPersisted() {
		List<Application> all = this.dao.findAll();

		assertTrue("findAll() returns list with correct size",
				all.size() == 0);
	}

	/**
	 * Tests whether findAll() gives us a correct list when something is
	 * persisted.
	 */
	@Test
	public void findAll_shouldReturnCorrectResultWhenSthPersisted() {
		Application app1 = this.getRandomSavedMockApp();
		Application app2 = this.getRandomSavedMockApp();

		List<Application> all = this.dao.findAll();

		assertTrue("findAll() returns list with correct size",
				all.size() == 2);

		assertTrue("First app is contained when all requested",
				all.contains(app1));
		assertTrue("Second app is contained when all requested",
				all.contains(app2));
	}

	/**
	 *Tests whether findByCriteria() can deal with strange arguments.
	 */
	@Test
	public void findByCriteria_dealsAsExpectedWithVariousArguments() {
		@SuppressWarnings("unused")
		List<Application> got;
		Criterion c = Restrictions.eq("id", 1);

		got = dao.findByCriteria();
		assertTrue("findByCriteria() doesn't throw when no argument",
				true);

		got = dao.findByCriteria((Criterion) null);
		assertTrue("findByCriteria() doesn't throw when null argument",
				true);

		got = dao.findByCriteria(c);
		assertTrue("findByCriteria() doesn't throw when sane argument",
				true);

		got = dao.findByCriteria(c, (Criterion) null);
		assertTrue("findByCriteria() doesn't throw when arguments mixed",
				true);
	}

	/**
	 * Tests whether findByCriteria() returns correct results when fed with a
	 * simple {@link Criterion}.
	 */
	@Test
	public void findByCriteria_canHandleSimpleCriteria() {
		Application app1 = getRandomSavedMockApp();
		Application app2 = getRandomSavedMockApp();
		Criterion c1 = Restrictions.eq("name", app1.getName());

		List<Application> got = dao.findByCriteria(c1);

		assertTrue("findByCriteria() returned expected number of apps",
				got.size() == 1);
		assertEquals("findByCriteria() returned expected application",
				got.get(0), app1);
		assertNotEquals("findByCriteria() returned expected application",
				got.get(0), app2);
	}

	/**
	 * Tests whether findByCriteria() returns correct results when fed with a
	 * rather complex {@link Criterion}.
	 */
	@Test
	public void findByCriteria_canHandleComplexCriteria() {
		Application app1 = getRandomSavedMockApp(); // should be returned
		Application app2 = getRandomSavedMockApp(); // should not be returned
		Application app3 = getRandomSavedMockApp(); // should be returned

		Criterion c1 = Restrictions.eq("name", app1.getName());
		Criterion c3 = Restrictions.eq("modified", app3.getModified());

		Disjunction or = Restrictions.disjunction();

		or.add(c1).add(c3);

		List<Application> got = dao.findByCriteria(or);

		assertTrue("findByCriteria() returned expected number of apps",
				got.size() == 2);
		assertTrue("findByCriteria() returned expected applications (app1)",
				got.contains(app1));
		assertFalse("findByCriteria() returned expected applications (app2)",
				got.contains(app2));
		assertTrue("findByCriteria() returned expected applications (app3)",
				got.contains(app3));
	}

	/**
	 * Tests whether findByCriteria() returns correct results when fed with two
	 * simple {@link Criterion}s.
	 */
	@Test
	public void findByCriteria_canHandleManyCriterias() {
		Application app1 = getRandomSavedMockApp(); // should be returned
		Application app2 = getRandomSavedMockApp(); // should not be returned

		Criterion c1name = Restrictions.eq("name", app1.getName());
		Criterion c1modified = Restrictions.eq("modified", app1.getModified());

		List<Application> got = dao.findByCriteria(c1name, c1modified);

		assertTrue("findByCriteria() returned expected number of apps",
				got.size() == 1);
		assertTrue("findByCriteria() returned expected applications (app1)",
				got.contains(app1));
		assertFalse("findByCriteria() returned expected applications (app2)",
				got.contains(app2));
	}
}
