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

import de.terrestris.shogun2.dao.LayerGroupDao;
import de.terrestris.shogun2.model.layer.AbstractLayer;
import de.terrestris.shogun2.model.layer.LayerGroup;
import de.terrestris.shogun2.service.LayerGroupService;
import de.terrestris.shogun2.util.data.ResultSet;

/**
 *
 * @author Johannes Weskamm
 * @author Kai Volland
 * @author Nils Bühner
 *
 */
@Controller
@RequestMapping("/layergroup")
public class LayerGroupController<E extends LayerGroup, D extends LayerGroupDao<E>, S extends LayerGroupService<E, D>>
		extends AbstractLayerController<E, D, S> {

	/**
	 * Default constructor, which calls the type-constructor
	 */
	@SuppressWarnings("unchecked")
	public LayerGroupController() {
		this((Class<E>) LayerGroup.class);
	}

	/**
	 * Constructor that sets the concrete entity class for the controller.
	 * Subclasses MUST call this constructor.
	 */
	protected LayerGroupController(Class<E> entityClass) {
		super(entityClass);
	}

	/**
	 * We have to use {@link Qualifier} to define the correct service here.
	 * Otherwise, spring can not decide which service has to be autowired here
	 * as there are multiple candidates.
	 */
	@Override
	@Autowired
	@Qualifier("layerGroupService")
	public void setService(S service) {
		this.service = service;
	}

	/**
	 *
	 * @param moduleId
	 * @param toolIds
	 * @return
	 */
	@RequestMapping(value = "/setLayersForLayerGroup.action", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> setLayersForLayerGroup(
			@RequestParam("layerGroupId") Integer layerGroupId,
			@RequestParam("abstractLayerIds") List<Integer> abstractLayerIds) {

		try {
			List<AbstractLayer> layers = this.service.setLayersForLayerGroup(layerGroupId, abstractLayerIds);
			return ResultSet.success(layers);
		} catch (Exception e) {
			return ResultSet.error("Could not set Layers for LayerGroup");
		}
	}

	/**
	 *
	 * @param abstractLayerId
	 * @return
	 */
	@RequestMapping(value = "/getLayerGroupsOfLayer.action", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getLayerGroupsOfLayer(Integer abstractLayerId) {

		try {
			 Set<E> layergroups = this.service.findLayerGroupsOfAbstractLayer(abstractLayerId);
			return ResultSet.success(layergroups);
		} catch (Exception e) {
			return ResultSet.error("Could not get Layergroups of layer with id " + abstractLayerId + ".");
		}
	}

}
