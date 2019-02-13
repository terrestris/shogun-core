#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

{package}.dao.ProjectApplicationDao;
import ${package}.model.ProjectApplication;
{package}.service.ProjectApplicationService;
import de.terrestris.shoguncore.rest.ApplicationRestController;

/**
 * This is a demo controller that demonstrates how SHOGun-Core REST controllers
 * can be extended.
 *
 * @author Nils Bühner
 *
 */
@RestController
@RequestMapping("/projectapplications")
public class ProjectApplicationRestController<E extends ProjectApplication, D extends ProjectApplicationDao<E>, S extends ProjectApplicationService<E, D>>
		extends ApplicationRestController<E, D, S> {

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("projectApplicationService")
	public void setService(S service) {
		this.service = service;
	}

}
