package de.terrestris.shogun2.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

import de.terrestris.shogun2.model.PersistentObject;

/**
 * Service class to describe classes model.
 *
 * terrestris GmbH & Co. KG
 * @author Kai Volland
 * @date 04.09.2017
 *
 */
@Service("modelDescriptionService")
public class ModelDescriptionService<E extends PersistentObject> {
	
	public JsonSchema getJsonSchema(Class<?> clazz) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);

		JsonSchema schema = schemaGen.generateSchema(clazz);

		return schema;
	}


}
