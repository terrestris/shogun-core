package de.terrestris.shogun2.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shogun2.dao.WpsProcessExecuteDao;
import de.terrestris.shogun2.model.wps.WpsProcessExecute;
import de.terrestris.shogun2.service.WpsProcessExecuteService;

/**
 * 
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
 *
 * @param <E>
 * @param <D>
 * @param <S>
 */
public class WpsProcessExecuteIdResolver<E extends WpsProcessExecute, D extends WpsProcessExecuteDao<E>, 
		S extends WpsProcessExecuteService<E, D>> extends
		PersistentObjectIdResolver<E, D, S> {

	@Override
	@Autowired
	@Qualifier("wpsProcessExecuteService")
	public void setService(S service) {
		this.service = service;
	}

}
