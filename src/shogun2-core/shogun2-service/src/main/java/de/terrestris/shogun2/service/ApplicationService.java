package de.terrestris.shogun2.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.terrestris.shogun2.dao.ApplicationDao;
import de.terrestris.shogun2.model.Application;

/**
 * @author Nils Bühner
 *
 */
@Service("applicationService")
@Transactional(readOnly = true)
public class ApplicationService {

	private static final Logger LOG = Logger
			.getLogger(ApplicationService.class);

	@Autowired
	ApplicationDao applicationDao;

	@Transactional(readOnly = false)
	public Application createApplication(Application application) {

		applicationDao.saveOrUpdate(application);

		LOG.info("Saved Application: " + application);

		return application;
	}

	public List<Application> findAllApplications() {
		return applicationDao.findAll();
	}

	public List<Application> findApplicationsLike(String name) {
		Criterion criterion = Restrictions.ilike("name", "%" + name + "%");
		return applicationDao.findByCriteria(criterion);
	}

	public Application findById(Integer id) {
		return applicationDao.findById(id);
	}

}
