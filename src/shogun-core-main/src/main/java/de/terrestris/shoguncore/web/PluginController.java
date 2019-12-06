/**
 *
 */
package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.dao.PluginDao;
import de.terrestris.shoguncore.model.Plugin;
import de.terrestris.shoguncore.service.PluginService;
import de.terrestris.shoguncore.util.data.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Nils Bühner
 */
@Controller
@RequestMapping("/plugins")
public class PluginController<E extends Plugin, D extends PluginDao<E>, S extends PluginService<E, D>>
    extends AbstractWebController<E, D, S> {

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public PluginController() {
        this((Class<E>) Plugin.class);
    }

    /**
     * Constructor that sets the concrete entity class for the controller.
     * Subclasses MUST call this constructor.
     */
    protected PluginController(Class<E> entityClass) {
        super(entityClass);
    }

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Override
    @Autowired
    @Qualifier("pluginService")
    public void setService(S service) {
        this.service = service;
    }

    /**
     * Checks in which applications the given plugin is contained (and from which it
     * would be removed in case of deletion).
     *
     * @param pluginId
     * @return
     */
    @RequestMapping(value = "preCheckDelete.action", method = RequestMethod.POST)
    public ResponseEntity<?> preCheckDelete(@RequestParam("pluginId") Integer pluginId) {
        List<String> result = null;
        try {
            result = service.preCheckDelete(pluginId);
        } catch (Exception e) {
            final String msg = e.getMessage();
            logger.error("Could not pre-check plugin deletion: " + msg);
            return new ResponseEntity<>(ResultSet.error(msg), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ResultSet.success(result), HttpStatus.OK);
    }

}
