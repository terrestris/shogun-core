/**
 *
 */
package de.terrestris.shogun2.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.terrestris.shogun2.dao.MapDao;
import de.terrestris.shogun2.model.layer.Layer;
import de.terrestris.shogun2.model.module.Map;
import de.terrestris.shogun2.service.MapService;
import de.terrestris.shogun2.util.data.ResultSet;

/**
 * @author Johannes Weskamm
 * @author Kai Volland
 */
@Controller
@RequestMapping("/maps")
public class MapController<E extends Map, D extends MapDao<E>, S extends MapService<E, D>> {

    protected S service;

    /**
     * We have to use {@link Qualifier} to define the correct service here.
     * Otherwise, spring can not decide which service has to be autowired here
     * as there are multiple candidates.
     */
    @Autowired
    @Qualifier("mapService")
    public void setService(S service) {
        this.service = service;
    }

    /**
     * Default constructor, which calls the type-constructor
     */
    @SuppressWarnings("unchecked")
    public MapController() {
        this((Class<E>) Map.class);
    }

    /**
     * Constructor that sets the concrete type for this controller.
     * Subclasses MUST call this constructor.
     */
    protected MapController(Class<E> type) {
        super();
    }

    /**
     * @param moduleId
     * @param toolIds
     * @return
     */
    @RequestMapping(value = "/setLayersForMap.action", method = RequestMethod.POST)
    public @ResponseBody
    java.util.Map<String, Object> setLayersForMap(
        @RequestParam("mapModuleId") Integer mapModuleId,
        @RequestParam("layerIds") List<Integer> layerIds) {

        try {
            List<Layer> layers = this.service.setLayersForMap(mapModuleId, layerIds);
            return ResultSet.success(layers);
        } catch (Exception e) {
            return ResultSet.error("Could not set Layers for Map");
        }
    }

}
