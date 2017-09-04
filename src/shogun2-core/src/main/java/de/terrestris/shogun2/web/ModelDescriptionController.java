package de.terrestris.shogun2.web;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;

import de.terrestris.shogun2.service.ModelDescriptionService;
import de.terrestris.shogun2.util.data.ResultSet;

/**
 *
 * terrestris GmbH & Co. KG
 * @author Kai Volland
 * @date 04.09.2017
 *
 *
 */
@Controller
@RequestMapping("/describeModel")
public class ModelDescriptionController {

	/**
	 * The LOGGER instance (that will be available in all subclasses)
	 */
	protected final Logger LOG = Logger.getLogger(getClass());

	@Autowired
	@Qualifier("modelDescriptionService")
	private ModelDescriptionService<?> modelDescriptionService;

	@RequestMapping(value = "/asJson/{className}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getJsonSchema(@PathVariable String className) {
		try {

			// TODO Get these from bean or on instantiation
			String[] searchPackages = {
				"de.terrestris.shogun2.model",
				"de.terrestris.bismap.model"
			};

			// See https://stackoverflow.com/a/33111503
			Class<?> foundClass = null;
			for(int i=0; i < searchPackages.length; i++){
				try{
					boolean wasNull = foundClass == null;
					foundClass = Class.forName(searchPackages[i] + "." + className);
					if (!wasNull) throw new RuntimeException(className + " exists in multiple packages!");
				} catch (ClassNotFoundException e){
					//not in this package, try another
				}
			}

			JsonSchema json = modelDescriptionService.getJsonSchema(foundClass);

			return ResultSet.success(json);
		} catch (Exception e) {
			return ResultSet.error("Could not get description for " + className + " " + e.getMessage());
		}
	}

}
