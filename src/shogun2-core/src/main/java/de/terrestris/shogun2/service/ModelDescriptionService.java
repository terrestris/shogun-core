package de.terrestris.shogun2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;


/**
 * Service class to describe classes model.
 * <p>
 * terrestris GmbH & Co. KG
 *
 * @author Kai Volland
 * @date 04.09.2017
 */
@Service("modelDescriptionService")
public class ModelDescriptionService {

    /**
     * The LOGGER instance (that will be available in all subclasses)
     */
    protected final Logger LOG = getLogger(getClass());

    @Resource
    @Qualifier("describeModelSearchPackages")
    private List<String> describeModelSearchPackages;

    /**
     *
     */
    @Autowired
    protected ObjectMapper objectMapper;

    public JsonSchema getJsonSchema(String className) throws IOException {

        Class<?> foundClass = null;
        for (String searchPackage : describeModelSearchPackages) {
            LOG.debug(String.format("Search className %s in package %s.", className, searchPackage));
            try {
                boolean wasNull = foundClass == null;
                foundClass = Class.forName(searchPackage + "." + className);
                if (!wasNull) {
                    LOG.error(String.format("Modelname %s exists in multiple packages! Last one will win.", className));
                }
            } catch (ClassNotFoundException e) {
                //not in this package, try another
            }
        }

        if (foundClass == null) {
            LOG.warn(String.format("No class found for describing modelname %s", className));
            return null;
        }

        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(objectMapper);
        JsonSchema schema = schemaGen.generateSchema(foundClass);

        return schema;
    }

}
