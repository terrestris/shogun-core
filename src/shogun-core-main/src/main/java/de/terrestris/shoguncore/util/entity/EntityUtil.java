package de.terrestris.shoguncore.util.entity;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * @author Nils Bühner
 * @author terrestris GmbH & Co. KG
 */
public class EntityUtil {

    /**
     * The parameter that might contain a list of fieldNames to restrict the entity to.
     */
    public static final String RESTRICT_FIELDS_PARAM = "output:only";

    /**
     * @param clazz
     * @param fieldName
     * @param fieldEntityType
     * @param forceAccess
     */
    public static boolean isField(Class<?> clazz, String fieldName, Class<?> fieldEntityType, boolean forceAccess) {
        Field field = FieldUtils.getField(clazz, fieldName, forceAccess);
        if (field == null) {
            return false;
        }

        final Class<?> fieldType = field.getType();

        // we'll also return true if the fieldEntityType is null, i.e. "unknown"
        return fieldEntityType == null || fieldType.isAssignableFrom(fieldEntityType);
    }

    /**
     * Checks whether the given <code>fieldName</code> in <code>clazz</code> is
     * a collection field with elements of type
     * <code>collectionElementType</code>.
     *
     * @param clazz                 The class to check for the given collection field
     * @param fieldName             The name of the collection field
     * @param collectionElementType The type of the concrete element in the collection
     * @param forceAccess           whether to break scope restrictions using the
     *                              {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                              method. {@code false} will only match {@code public} fields.
     * @return Whether or not the given <code>fieldName</code> in
     * <code>clazz</code> is a collection field with elements of type
     * <code>collectionElementType</code>.
     */
    public static boolean isCollectionField(Class<?> clazz, String fieldName, Class<?> collectionElementType,
                                            boolean forceAccess) {
        Field field = FieldUtils.getField(clazz, fieldName, forceAccess);
        if (field == null) {
            return false;
        }
        boolean isCollectionField = false;

        if (Collection.class.isAssignableFrom(field.getType())) {
            ParameterizedType collType = (ParameterizedType) field.getGenericType();
            Class<?> elementTypeOfCollection = (Class<?>) collType.getActualTypeArguments()[0];
            isCollectionField = elementTypeOfCollection.isAssignableFrom(collectionElementType);
        }

        return isCollectionField;
    }

    /**
     * Returns a list of fieldNames of the passed class that can be used to either
     * <p>
     * <ul>
     * <li>filter results with, see {@link #validFieldNamesWithCastedValues}, or</li>
     * <li>restrict output of queries, see {@link #determineRestrictFields} </li>
     * </ul>
     *
     * @param entityClass
     * @return
     */
    public static List<String> getFilterableOrRestrictableFieldNames(Class<?> entityClass) {
        List<Field> allFields = FieldUtils.getAllFieldsList(entityClass);
        List<String> restrictableFields = new ArrayList<String>();
        for (Field field : allFields) {
            final Class<?> fieldType = field.getType();
            final String fieldName = field.getName();
            final int fieldModifiers = field.getModifiers();

            final boolean isPrimitiveOrWrapper = ClassUtils.isPrimitiveOrWrapper(fieldType);
            final boolean isString = fieldType.equals(String.class);
            final boolean isStatic = Modifier.isStatic(fieldModifiers);
            final boolean isPrivate = Modifier.isPrivate(fieldModifiers);

            // extract only non-static private fields that are primitive or
            // primitive wrapper types or String
            if ((isPrimitiveOrWrapper || isString) && isPrivate && !isStatic) {
                restrictableFields.add(fieldName);
            }
        }
        return restrictableFields;
    }

    /**
     * A small utility method that will turn a passed list with comma separated strings into
     * a list which has single elements:
     * <p>
     * Examples:
     * <p>
     * <table>
     *     <caption>Examples:</caption>
     * <thead>
     * <tr>
     * <th>in</th><th>out</th><th>in.size()</th><th>out.size()</th>
     * </tr>
     * </thead>
     * <tbody>
     * <tr>
     * <td><code>null</code></td><td><code>null</code></td>
     * <td>n.a.</td><td>n.a.</td>
     * </tr>
     * <tr>
     * <td><code>[null]</code></td><td><code>null</code></td>
     * <td>1</td><td>n.a.</td>
     * </tr>
     * <tr>
     * <td><code>["foo"]</code></td><td><code>["foo"]</code></td>
     * <td>1</td><td>1</td>
     * </tr>
     * <tr>
     * <td><code>["foo,bar"]</code></td>
     * <td><code>["foo", "bar"]</code></td>
     * <td>1</td><td>2</td>
     * </tr>
     * <tr>
     * <td><code>["foo", "bar"]</code></td>
     * <td><code>["foo", "bar"]</code></td>
     * <td>2</td><td>2</td>
     * </tr>
     * <tr>
     * <td><code>["foo,humpty", "bar,dumpty"]</code></td>
     * <td><code>["foo", "humpty", "bar", "dumpty"]</code></td>
     * <td>2</td><td>4</td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @param listOfCommaSeparatedValues
     */
    public static List<String> listFromCommaSeparatedStringList(List<String> listOfCommaSeparatedValues) {
        if (listOfCommaSeparatedValues == null) {
            return null;
        }
        List<String> outList = new ArrayList<String>();
        for (String commaSeparatedValues : listOfCommaSeparatedValues) {
            if (commaSeparatedValues != null) {
                List<String> values = Arrays.asList(
                    commaSeparatedValues.split("\\s*,\\s*")
                );
                outList.addAll(values);
            }
        }
        if (outList.size() == 0) {
            return null;
        }
        return outList;
    }

    /**
     * Returns a list of fieldnames to restrict the output to. This list is the
     * intersection of fields that are possible to be restricted and the names
     * that are actually requested in the <code>requestedFilter</code>.
     * <p>
     * Only the key {@value RESTRICT_FIELDS_PARAM} is taken as list of requested
     * fieldName to restrict by, the value is treated in a case-insensitive matter.
     * <p>
     * The returned list will have casing as they appear in the class, regardless
     * of the case of the values that appear in <code>RESTRICT_FIELDS_PARAM</code>.
     *
     * @param requestedFilter
     * @param entityClass
     */
    public static List<String> determineRestrictFields(MultiValueMap<String, String> requestedFilter, Class<?> entityClass) {
        if (requestedFilter == null) {
            return null;
        }
        List<String> restrictFieldsTo = null;
        Set<String> keys = requestedFilter.keySet();
        for (String key : keys) {
            if (RESTRICT_FIELDS_PARAM.equalsIgnoreCase(key)) {
                restrictFieldsTo = listFromCommaSeparatedStringList(
                    requestedFilter.get(key)
                );
            }
        }
        if (restrictFieldsTo == null) {
            return null;
        }

        List<String> restrictableFieldNames = getFilterableOrRestrictableFieldNames(entityClass);
        List<String> filteredRestrictTo = new ArrayList<String>();

        for (String restrictableFieldName : restrictableFieldNames) {
            for (String requestedRestrictTo : restrictFieldsTo) {
                if (restrictableFieldName.equalsIgnoreCase(requestedRestrictTo)) {
                    filteredRestrictTo.add(restrictableFieldName);
                }
            }
        }

        if (filteredRestrictTo.size() == 0) {
            filteredRestrictTo = null;
        }

        return filteredRestrictTo;
    }

    /**
     * This method returns a multi value map, where the keys are the
     * intersection of (non-static private) field names of the given entity
     * class and the given set of requested/input field names (which are the
     * keys of the passed multi value map), i.e. fields in the input that are
     * not present in the entity model definition will be removed/ignored in
     * this method.
     * <p>
     * Regarding case sensitivity, the field names that are building the keys of
     * the result map are used in their original representation/definition from
     * the entity model, but the keys/fieldnames in the input may be
     * case-insensitive.
     * <p>
     * The value for each key is a list of casted (!) values of the string
     * values of the given input map. The type of the field in the entity is
     * used to determine the correct casting.
     *
     * @param requestedFilter
     * @param entityClass
     * @return
     */
    public static MultiValueMap<String, Object> validFieldNamesWithCastedValues(MultiValueMap<String, String> requestedFilter, Class<?> entityClass) {

        Set<String> inputFieldNames = requestedFilter.keySet();

        // Regarding case insensitivity: build a map that maps from the input field name to its original field name,
        // but add only those fields to the map that exist in the entity
        Map<String, String> validInputFieldNameToOrigFieldName = new HashMap<>();

        List<String> filterableFieldNames = getFilterableOrRestrictableFieldNames(entityClass);
        for (String filterableFieldName : filterableFieldNames) {
            // find the corresponding field in the input
            for (String inputFieldName : inputFieldNames) {
                if (filterableFieldName.equalsIgnoreCase(inputFieldName)) {
                    validInputFieldNameToOrigFieldName.put(inputFieldName, filterableFieldName);
                    break;
                }
            }
        }

        MultiValueMap<String, Object> result = new LinkedMultiValueMap<>();

        Set<String> validInputFieldNames = validInputFieldNameToOrigFieldName.keySet();

        for (String validInputFieldName : validInputFieldNames) {
            String origInputFieldName = validInputFieldNameToOrigFieldName.get(validInputFieldName);

            List<String> stringValues = requestedFilter.get(validInputFieldName);

            // cast to the correct type to avoid hibernate exceptions when querying db or similar
            for (String fieldStringValue : stringValues) {
                Field f = FieldUtils.getField(entityClass, origInputFieldName, true);
                Class<?> fieldType = f.getType();
                Object castedValue = ConvertUtils.convert(fieldStringValue, fieldType);
                result.add(origInputFieldName, castedValue);
            }
        }

        return result;
    }
}
