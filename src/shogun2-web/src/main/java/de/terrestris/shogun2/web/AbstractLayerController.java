/**
 *
 */
package de.terrestris.shogun2.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.terrestris.shogun2.dao.AbstractLayerDao;
import de.terrestris.shogun2.model.layer.AbstractLayer;
import de.terrestris.shogun2.service.AbstractLayerService;
import de.terrestris.shogun2.util.data.ResultSet;

/**
 * @author Johannes Weskamm
 * @author Kai Volland
 *
 */
@Controller
@RequestMapping("/abstractlayers")
public class AbstractLayerController<E extends AbstractLayer, D extends AbstractLayerDao<E>, S extends AbstractLayerService<E, D>>{

	protected S service;

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Autowired
	@Qualifier("abstractLayerService")
	public void setService(S service) {
		this.service = service;
	}

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public AbstractLayerController() {
		this((Class<E>) AbstractLayer.class);
	}

	/**
	 * Constructor that sets the concrete type for this controller.
	 * Subclasses MUST call this constructor.
	 */
	protected AbstractLayerController(Class<E> type) {
		super();
	}

	/**
	 * 
	 * @param abstractLayerId
	 * @return
	 */
	@RequestMapping(value = "/getLayerGroupsOfLayer.action", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getLayerGroupsOfLayer(Integer abstractLayerId) {

		AbstractLayer abstractLayer = this.service.findById(abstractLayerId);
		try {
			 Set<E> layergroups = this.service.findLayerGroupsOfAbstractLayer(abstractLayer);
			return ResultSet.success(layergroups);
		} catch (Exception e) {
			return ResultSet.error("Could not get Layergroups of layer " + abstractLayer.getName() + ".");
		}
	}

	/**
	 * 
	 * @param moduleId
	 * @param toolIds
	 * @return
	 */
	@RequestMapping(value = "/setLayersForLayerGroup.action", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> setLayersForLayerGroup(
			Integer layerGroupId, @RequestParam("abstractLayerIds") List<Integer> abstractLayerIds) {

		try {
			List<AbstractLayer> layers = this.service.setLayersForLayerGroup(layerGroupId, abstractLayerIds);
			return ResultSet.success(layers);
		} catch (Exception e) {
			return ResultSet.error("Could not set Layers for LayerGroup");
		}
	}
	
}
