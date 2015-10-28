package de.terrestris.shogun2.web;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.terrestris.shogun2.model.layout.Layout;
import de.terrestris.shogun2.service.LayoutService;

/**
 * @author Nils Bühner
 * 
 */
@Controller
@RequestMapping("/layout")
public class LayoutController {

	private static final Logger LOG = Logger.getLogger(LayoutController.class);

	@Autowired
	private LayoutService LayoutService;

	@RequestMapping(value = "/findAll.action", method = RequestMethod.GET)
	public @ResponseBody List<Layout> findAllLayouts() {
		LOG.info("Trying to find all Layouts.");

		return LayoutService.findAll();
	}
}
