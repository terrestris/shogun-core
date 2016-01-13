#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ${package}.model.ProjectApplication;
import ${package}.dao.ProjectApplicationDao;
import ${package}.service.ProjectApplicationService;
import de.terrestris.shogun2.web.ApplicationController;

/**
 * This is a demo controller that demonstrates how a SHOGun2 controllers can be
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
}
