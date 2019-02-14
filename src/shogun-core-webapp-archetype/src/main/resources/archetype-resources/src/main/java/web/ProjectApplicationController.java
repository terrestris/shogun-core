#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ${package}.model.ProjectApplication;
import ${package}.dao.ProjectApplicationDao;
import ${package}.service.ProjectApplicationService;
import de.terrestris.shoguncore.web.ApplicationController;

/**
 * This is a demo controller that demonstrates how a SHOGun-Core controller can be
 * extended.
 *
 * @author Nils Bühner
 *
 * @param <E>
 * @param <D>
 * @param <S>
 */
@Controller
@RequestMapping("/projectApplication")
public class ProjectApplicationController<E extends ProjectApplication, D extends ProjectApplicationDao<E>, S extends ProjectApplicationService<E, D>>
		extends ApplicationController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public ProjectApplicationController() {
		this((Class<E>) ProjectApplication.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected ProjectApplicationController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("projectApplicationService")
	public void setService(S service) {
		super.setService(service);
	}

	/**
	 * A demo interface for the creation of a new project application. Can be
	 * used as a showcase that the project specific permission evaluators for
	 * the CREATE permission are really working ;-) You should set the LOG level
	 * to TRACE to see the logs of the permission evaluators (when logged in as
	 * a NON-admin, e.g. "user").
	 *
	 * @param name
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@RequestMapping(value = "create.action", method = RequestMethod.GET)
	public @ResponseBody E create(String name)
			throws InstantiationException, IllegalAccessException {
		E app = getEntityClass().newInstance();
		app.setName(name);
		service.saveOrUpdate(app);
		return app;
	}
}
