package de.terrestris.shogun2.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Repository;

import de.terrestris.shogun2.model.Application;

@Repository("applicationDao")
public class ApplicationDao<E extends Application> extends
		GenericHibernateDao<E, Integer> {

	/**
	 * Public default constructor for this DAO.
	 */
	@SuppressWarnings("unchecked")
	public ApplicationDao() {
		super((Class<E>) Application.class);
	}

	/**
	 * Constructor that has to be called by subclasses.
	 *
	 * @param clazz
	 */
	protected ApplicationDao(Class<E> clazz) {
		super(clazz);
	}

	/**
	 * Returns all applications that are NOT templates!
	 *
	 * @return All appliocations that are NOT templates.
	 */
	@Override
	public List<E> findAll() {
		LOG.trace("Finding all (non-template) applications.");
		SimpleExpression areNotTemplate =
				Restrictions.ne("template", true); // everything but true (i.e. false or null)
		return findByCriteria(areNotTemplate);
	}

	/**
	 * Returns all applications that are templates!
	 *
	 * @return All applications that are templates.
	 */
	public List<E> findAllTemplates() {
		LOG.trace("Finding all template applications.");
		SimpleExpression areTemplate =
				Restrictions.eq("template", true);
		return findByCriteria(areTemplate);
	}

}
