package de.terrestris.shoguncore.web;

import de.terrestris.shoguncore.dao.MapDao;
import de.terrestris.shoguncore.model.layer.Layer;
import de.terrestris.shoguncore.model.module.Map;
import de.terrestris.shoguncore.service.MapService;
import de.terrestris.shoguncore.util.data.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Johannes Weskamm
 * @author Kai Volland
 */
@Controller
@RequestMapping("/maps")
public class MapController<E extends Map, D extends MapDao<E>, S extends MapService<E, D>> {

    public static final String COULD_NOT_SET_ERROR_MSG = "Could not set layers for Map";

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
     * Set layers for map
     *
     * @param mapModuleId The map module id
     * @param layerIds The list of layer ids
     * @return
     */
    @RequestMapping(value = "/setLayersForMap.action", method = RequestMethod.POST)
    public @ResponseBody
    java.util.Map<String, Object> setLayersForMap(
        @RequestParam("mapModuleId") Integer mapModuleId,
        @RequestParam("layerIds") List<Integer> layerIds) {
        try {
            if (mapModuleId == null || layerIds == null || layerIds.isEmpty()) {
                throw new Exception();
            }
            List<Layer> layers = this.service.setLayersForMap(mapModuleId, layerIds);
            return ResultSet.success(layers);
        } catch (Exception e) {
            return ResultSet.error(COULD_NOT_SET_ERROR_MSG);
        }
    }

}
