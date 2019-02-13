/**
 *
 */
package de.terrestris.shogun2.converter;

import org.apache.commons.lang3.math.NumberUtils;

import javax.persistence.AttributeConverter;

/**
 * This converter can be used for the values of the type Map<String, Object>.
 * <p>
 * The values of the map will be converted to strings to persist them in a
 * string column in the database. A string value coming from the database will
 * be converted to the best matching Java type.
 * <p>
 * Currently {@link String}, {@link Long}, {@link Double} and {@link Boolean}
 * values are supported.
 *
 * @author Nils Bühner
 */
public class PropertyValueConverter implements AttributeConverter<Object, String> {

    /**
     * Converts an arbitrary object to it's string representation, that will be
     * stored in the database.
     */
    @Override
    public String convertToDatabaseColumn(Object attribute) {
        return attribute.toString();
    }

    /**
     * Converts a string value from the database to the best matching java
     * primitive type.
     */
    @Override
    public Object convertToEntityAttribute(String dbData) {
        if ("true".equalsIgnoreCase(dbData)) {
            return true;
        } else if ("false".equalsIgnoreCase(dbData)) {
            return false;
        } else if (NumberUtils.isParsable(dbData)) {
            if (NumberUtils.isDigits(dbData) ||
                (dbData.startsWith("-") &&
                    NumberUtils.isDigits(dbData.substring(1)))) {
                return Long.parseLong(dbData);
            } else {
                return Double.parseDouble(dbData);
            }
        }
        return dbData;
    }

}
