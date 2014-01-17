package de.terrestris.shogun2.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.ApplicationDao;
import de.terrestris.shogun2.model.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/test-context-dao.xml" })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class ApplicationDaoTest {

	@Autowired
	ApplicationDao applicationDao;

	@Test
	public void saveOrUpdate_shouldSaveOrUpdateApplication() {
		Application application = new Application();
		application.setName("DaoTestApplication");
		application.setDescription("Description of DaoTestApplication");

		assertNull(application.getId());

		// Test CREATE
		applicationDao.saveOrUpdate(application);

		Integer id = application.getId();

		assertNotNull(id);
		assertTrue(id > 0);

		// Test UPDATE
		String changedNameOfApp = "Changed name of DaoTestApplication";
		String changedDescOfApp = "Changed description of DaoTestApplication";

		application.setName(changedNameOfApp);
		application.setDescription(changedDescOfApp);

		applicationDao.saveOrUpdate(application);

		assertEquals(id, application.getId());
		assertEquals(changedNameOfApp, application.getName());
		assertEquals(changedDescOfApp, application.getDescription());
	}
}
