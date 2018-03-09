package de.terrestris.shogun2.web;

import java.util.Map;

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
 * terrestris GmbH & Co. KG
 *
 * @author Kai Volland
 * @date 04.09.2017
 */
@Controller
@RequestMapping("/describeModel")
public class ModelDescriptionController {

    @Autowired
    @Qualifier("modelDescriptionService")
    private ModelDescriptionService modelDescriptionService;

    @RequestMapping(value = "/asJson/{className}.action", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getJsonSchema(@PathVariable String className) {
        try {
            JsonSchema json = modelDescriptionService.getJsonSchema(className);
            if (json == null) {
                return ResultSet.error("Modeldescription (json) for model " + className + "is null.");
            }
            return ResultSet.success(json);
        } catch (Exception e) {
            return ResultSet.error("Could not get description for " + className + " " + e.getMessage());
        }
    }

}
