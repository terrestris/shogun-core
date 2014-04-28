package de.terrestris.shogun2.dao;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.ApplicationDao;
import de.terrestris.shogun2.model.Application;

/**
 * 
 * @author Nils Bühner
 * @author Marc Jansen
 * 
 *         This class will test the {@link ApplicationDao}.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-context-dao.xml" })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ApplicationDaoTest {

	@Autowired
	ApplicationDao applicationDao;

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

		applicationDao.saveOrUpdate(app);

		return app;
	}

	/**
	 * Tests whether we can both CREATE and UPDATE with saveOrUpdate
	 */
	@Test
	public void saveOrUpdate_shouldSaveOrUpdateApplication() {
		Application app = getRandomUnsavedMockApp();

		assertNull(app.getId());

		// Test CREATE
		applicationDao.saveOrUpdate(app);

		Integer id = app.getId();

		assertNotNull(id);
		assertTrue(id > 0);

		// Test UPDATE
		String changedNameOfApp = "Some other name";
		String changedDescOfApp = "Changed description";

		app.setName(changedNameOfApp);
		app.setDescription(changedDescOfApp);

		applicationDao.saveOrUpdate(app);

		assertEquals(id, app.getId());
		assertEquals(changedNameOfApp, app.getName());
		assertEquals(changedDescOfApp, app.getDescription());
	}

	/**
	 * Tests whether the automatic setting of field modified happens when
	 * creating.
	 */
	@Test
	public void saveOrUpdate_shouldSetModified() {
		// first create an application and save it.
		Application app = getRandomUnsavedMockApp();

		// Model tests should ensure that the constructor populates modified
		ReadableDateTime before = app.getModified();

		try {
			// ... wait ...
			Thread.sleep(50);

			// ... then save
			applicationDao.saveOrUpdate(app);

			ReadableDateTime after = app.getModified();

			// They should be different
			assertNotEquals(before, after);
			assertFalse(before.equals(after));
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
		Application app = getRandomUnsavedMockApp();

		try {
			// ... wait ...
			Thread.sleep(50);

			// ... then update the modified field
			DateTime before = DateTime.now();
			app.setModified(before);

			// ... wait ...
			Thread.sleep(50);

			// ... and only then save, modified should differ now
			applicationDao.saveOrUpdate(app);
			ReadableDateTime after = app.getModified();

			// They should be different
			assertNotEquals(before, after);
			assertFalse(before.equals(after));
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
		Application app = getRandomUnsavedMockApp();
		applicationDao.saveOrUpdate(app);

		ReadableDateTime before = app.getModified();

		try {
			Thread.sleep(50);
			// change the application
			app.setName("Some other name");
			app.setDescription("Changed description");
			applicationDao.saveOrUpdate(app);
			ReadableDateTime after = app.getModified();

			// They should be different
			assertNotEquals(before, after);
			assertFalse(before.equals(after));
			// after should be greater than before
			boolean isLater = before.compareTo(after) == -1;
			assertTrue(isLater);
		} catch (InterruptedException e) {
			fail("Caught exception while attempting to wait");
			return;
		}

	}

	/**
	 * Tests whether we can retrieve saved applications by id.
	 */
	@Test
	public void findById_shouldReturnNullForNonExistingId() {
		Application app = applicationDao.findById(-90210);
		assertNull(app);
	}

	/**
	 * Tests whether we can retrieve saved applications by id.
	 */
	@Test
	public void findById_shouldRetrieveApplication() {
		Application app = getRandomSavedMockApp();
		Integer id = app.getId();
		Application queriedApp = applicationDao.findById(id);

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

		Application queriedFirst = applicationDao.findById(firstId);
		Application queriedSecond = applicationDao.findById(secondId);

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
		int numBefore = applicationDao.findAll().size();

		// ... delete the app
		applicationDao.delete(app);

		int numAfter = applicationDao.findAll().size();

		// ...try to get it by id
		Application queriedAppAfter = applicationDao.findById(id);

		assertNull(queriedAppAfter);
		assertEquals(numBefore, numAfter + 1);

	}

	/**
	 * Tests whether deletion with unsaved apps doesnÄt throw an exception.
	 */
	@Test
	public void delete_shouldNotThrowWhenUnsavedAppIsPassed() {
		Application app = getRandomUnsavedMockApp();

		// ... delete the unsaved app
		applicationDao.delete(app);

		assertTrue(true);

	}
}
