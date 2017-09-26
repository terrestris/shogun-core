package de.terrestris.shogun2.hibernate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A JPA converter that can be used to convert jsonb fields into a map. Currently only works with 'simple' json
 * content (only one level objects like {"name": "peter", "someprop": 345}).
 *
 * Please note that you'll need to switch to pgjdbc-ng from https://github.com/impossibl/pgjdbc-ng instead of using the
 * standard postgres driver.
 *
 * Please also note that if you want optimal performance you'll need to add an index manually, hibernate can only create
 * btree indexes.
 */
@Converter(autoApply = true)
public class SimpleJsonbConverter implements AttributeConverter<Map<String, String>, String> {

    private static final Logger LOG = Logger.getLogger(SimpleJsonbConverter.class);

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return (mapper.writeValueAsString(attribute));
        } catch (Exception e) {
            LOG.warn("Could not convert JSON value to PostgreSQL format: " + e.getMessage());
            LOG.debug("Stack trace: " + e.toString());
        }
        return "{}";
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        ObjectMapper parser = new ObjectMapper();
        try {
            return parser.readValue(dbData, HashMap.class);
        } catch (IOException e) {
            LOG.warn("Could not convert JSON value from PostgreSQL format: " + e.getMessage());
            LOG.debug("Stack trace: " + e.toString());
        }
        return new HashMap<>();
    }

}
