package de.terrestris.shoguncore.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.terrestris.shoguncore.dao.WpsProcessExecuteDao;
import de.terrestris.shoguncore.model.wps.WpsProcessExecute;
import de.terrestris.shoguncore.service.WpsProcessExecuteService;

/**
 * @param <E>
 * @param <D>
 * @param <S>
 * @author Daniel Koch
 * @author terrestris GmbH & Co. KG
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
