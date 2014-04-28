package de.terrestris.shogun2.dao;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.model.Application;

/**
 * 
 * @author Marc Jansen
 * @author Nils Bühner
 * 
 *         This class will test the {@link GenericHibernateDao}. As
 *         {@link GenericHibernateDao} is an abstract class, we cannot
 *         instantiate it. Instead we will use the {@link ApplicationDao} (as an
 *         simple extension of {@link GenericHibernateDao}) to test the logic
 *         contained in {@link GenericHibernateDao}.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-context-dao.xml" })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class GenericHibernateDaoTest {

	/**
	 * We use the {@link ApplicationDao} to test the behaviour of the
	 * {@link GenericHibernateDao} (which is an abstract class and cannot be
	 * instantiated).
	 */
	@Autowired
	ApplicationDao applicationDao;

	/**
	 * Tests whether the automatic setting of field modified happens when
	 * creating.
	 */
	@Test
	public void saveOrUpdate_shouldSetModified() {
		// first create an application and save it.
		Application app = new Application("Some Name", "Some description");

		// Model tests should ensure that the constructor populates modified
		ReadableDateTime before = app.getModified();

		try {
			// ... wait ...
			Thread.sleep(1);

			// ... then save
			applicationDao.saveOrUpdate(app);

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
		Application app = new Application("Some Name", "Some description");

		try {
			// ... wait ...
			Thread.sleep(1);

			// ... then update the modified field
			DateTime before = DateTime.now();
			app.setModified(before);

			// ... wait ...
			Thread.sleep(1);

			// ... and only then save, modified should differ now
			applicationDao.saveOrUpdate(app);
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
		applicationDao.saveOrUpdate(app);

		ReadableDateTime before = app.getModified();

		try {
			Thread.sleep(1);
			// change the application
			app.setName("Some other name");
			app.setDescription("Changed description");
			applicationDao.saveOrUpdate(app);
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

}
